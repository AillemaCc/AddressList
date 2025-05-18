package org.AList.common.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.AList.domain.dao.entity.StudentDefaultInfoDO;
import org.AList.service.AdminExeclOpsService;
import org.AList.service.impl.AdminExeclOpsServiceImpl;
/**
 * 学籍数据的execlListener
 */
import java.util.List;
@Slf4j
public class StudentDefInfoListener extends AnalysisEventListener<StudentDefaultInfoDO> {
    private final AdminExeclOpsService adminExeclOpsService;
    public StudentDefInfoListener(AdminExeclOpsService adminExeclOpsService) {
        this.adminExeclOpsService = adminExeclOpsService;
    }


    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;
    /**
     * 缓存的数据
     */
    private List<StudentDefaultInfoDO> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    public StudentDefInfoListener(AdminExeclOpsServiceImpl adminExeclOpsService, AdminExeclOpsService excelService) {
        this.adminExeclOpsService = excelService;
    }
    /**
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        log.error("解析异常：", exception);
        throw exception;
    }

    /**
     */
    @Override
    public void invoke(StudentDefaultInfoDO studentDefaultInfoDO, AnalysisContext analysisContext) {
        log.info("解析到一条数据:{}", JSON.toJSONString(studentDefaultInfoDO));
        cachedDataList.add(studentDefaultInfoDO);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    /**
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        log.info("所有数据解析完成！");
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        adminExeclOpsService.saveStudentDefInfo(cachedDataList);
        log.info("存储数据库成功！");
    }
}
