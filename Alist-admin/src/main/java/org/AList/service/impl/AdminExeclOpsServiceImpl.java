package org.AList.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AList.common.convention.exception.ClientException;
import org.AList.common.listener.StudentDefInfoListener;
import org.AList.domain.dao.entity.StudentDefaultInfoDO;
import org.AList.domain.dao.mapper.StudentDefaultInfoMapper;
import org.AList.domain.dto.req.ExeclStudentExportConditionReqDTO;
import org.AList.service.AdminExeclOpsService;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * 管理员execl操作服务实现层
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminExeclOpsServiceImpl implements AdminExeclOpsService {
    private final SqlSessionFactory sqlSessionFactory;
    private final StudentDefaultInfoMapper studentDefaultInfoMapper;
    /**
     * 保存学生学籍数据到数据库
     */
    @Override
    public void saveStudentDefInfo(List<StudentDefaultInfoDO> cachedDataList) {
        if (CollectionUtils.isEmpty(cachedDataList)) {
            throw new ClientException("传入空数据列表，跳过保存操作");
        }
        log.info("开始批量保存{}条数据", cachedDataList.size());
        long startTime = System.currentTimeMillis();

        try (SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            StudentDefaultInfoMapper mapper = session.getMapper(StudentDefaultInfoMapper.class);

            // 分批处理防止超大事务
            int batchSize = 500;
            for (int i = 0; i < cachedDataList.size(); i++) {
                try {
                    mapper.insert(cachedDataList.get(i));

                    // 分段提交
                    if (i > 0 && i % batchSize == 0) {
                        session.commit();
                        session.clearCache();
                        log.debug("已提交{}条数据", i);
                    }
                } catch (Exception e) {
                    log.error("第{}条数据插入失败: {}", i, cachedDataList.get(i).getStudentId(), e);
                    // 可以在这里记录失败数据，继续处理后续记录
                }
            }
            session.commit();

            long costTime = System.currentTimeMillis() - startTime;
            log.info("成功保存{}条数据，耗时：{}ms", cachedDataList.size(), costTime);

        } catch (Exception e) {
            log.error("批量保存数据失败", e);
            throw new RuntimeException("数据保存失败，已回滚", e);
        }
    }

    /**
     * 解析学生学籍数据
     */
    @Override
    public void importStudentDefInfo(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new ClientException("上传文件不能为空");
        }
        StudentDefInfoListener listener = new StudentDefInfoListener(this);
        EasyExcel.read(file.getInputStream(), StudentDefaultInfoDO.class, listener)
                .sheet()
                .doRead();
    }

    @Override
    public void exportStudentDefInfo(HttpServletResponse response) {
        export(response, null);
    }

    @Override
    public void exportStudentDefInfoByCondition(HttpServletResponse response, ExeclStudentExportConditionReqDTO condition) {
        export(response, condition);
    }

    private void export(HttpServletResponse response, ExeclStudentExportConditionReqDTO condition) {
        try {
            // 1. 设置响应头
            setupResponseHeader(response);

            // 2. 查询数据
            List<StudentDefaultInfoDO> dataList = queryData(condition);

            // 3. 导出Excel
            EasyExcel.write(response.getOutputStream(), StudentDefaultInfoDO.class)
                    .sheet("学生学籍信息")
                    .doWrite(dataList);

            log.info("学籍信息导出成功，共{}条", dataList.size());
        } catch (Exception e) {
            log.error("学籍信息导出失败", e);
            throw new RuntimeException("导出失败: " + e.getMessage());
        }
    }



    private List<StudentDefaultInfoDO> queryData(ExeclStudentExportConditionReqDTO condition) {
        return Optional.ofNullable(condition)
                .map(studentDefaultInfoMapper::selectByCondition)
                .orElseGet(() -> studentDefaultInfoMapper.selectList(null));
    }

    private void setupResponseHeader(HttpServletResponse response) {
        String fileName = URLEncoder.encode("学生学籍信息_" + System.currentTimeMillis(), StandardCharsets.UTF_8);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
    }



}
