package org.AList.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.AList.common.biz.user.AdminContext;
import org.AList.domain.dao.entity.OperLogDO;
import org.AList.domain.dao.mapper.OperLogMapper;
import org.AList.domain.dto.req.AuditListReqDTO;
import org.AList.domain.dto.req.BoardQueryReqDTO;
import org.AList.domain.dto.resp.AdminHomePageRespDTO;
import org.AList.domain.dto.resp.AuditUserPageRespDTO;
import org.AList.domain.dto.resp.BoardQueryRespDTO;
import org.AList.domain.dto.resp.OperLogDTO;
import org.AList.service.AdminHomePageService;
import org.AList.service.AdministerAuditService;
import org.AList.service.BoardService;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

// Service实现
@Service
@RequiredArgsConstructor
public class AdminHomePageServiceImpl implements AdminHomePageService {
    private final AdministerAuditService administerAuditService;
    private final BoardService boardService;
    private final OperLogMapper operLogMapper;
      
    @Override  
    public AdminHomePageRespDTO getAdminHomePageData(Integer days) {
        // 1. 获取当前管理员用户名  
        String username = AdminContext.getAdminister();
          
        // 2. 获取待审核请求数量  
        Long pendingAuditCount = getPendingAuditCount();  
          
        // 3. 获取已发布公告数量  
        Long releasedBoardCount = getReleasedBoardCount();  
          
        // 4. 获取最近操作日志  
        List<OperLogDTO> recentOperations = getRecentOperations(days);
          
        // 5. 构建并返回结果  
        return AdminHomePageRespDTO.builder()  
                .username(username)  
                .pendingAuditCount(pendingAuditCount)  
                .releasedBoardCount(releasedBoardCount)  
                .recentOperations(recentOperations)  
                .build();  
    }  
      
    private Long getPendingAuditCount() {
        // 创建查询参数，只需要统计数量，所以页码和大小可以最小化
        AuditListReqDTO requestParam = new AuditListReqDTO();
        requestParam.setCurrent(1);
        requestParam.setSize(1);

        // 调用审核服务获取待审核请求的分页信息
        IPage<AuditUserPageRespDTO> auditPage = administerAuditService.listAuditRegister(requestParam);

        // 返回总记录数
        return auditPage.getTotal();
    }  
      
    private Long getReleasedBoardCount() {
        // 创建查询参数，只需要统计数量，所以页码和大小可以最小化
        BoardQueryReqDTO requestParam = new BoardQueryReqDTO();
        requestParam.setCurrent(1);
        requestParam.setSize(1);

        // 调用公告服务获取已发布公告的分页信息
        IPage<BoardQueryRespDTO> boardPage = boardService.queryAllReleased(requestParam);

        // 返回总记录数
        return boardPage.getTotal();
    }  
      
    private List<OperLogDTO> getRecentOperations(Integer days) {
        // 获取当前管理员用户名
        String username = AdminContext.getAdminister();

        // 计算days天前的时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        Date startTime = calendar.getTime();

        // 构建查询条件
        LambdaQueryWrapper<OperLogDO> queryWrapper = Wrappers.lambdaQuery(OperLogDO.class)
                .eq(OperLogDO::getOperName, username)
                .eq(OperLogDO::getStatus, 0) // 正常状态的日志
                .ge(OperLogDO::getOperTime, startTime)
                .orderByDesc(OperLogDO::getOperTime);

        // 查询最近的操作日志
        List<OperLogDO> operLogs = operLogMapper.selectList(queryWrapper);

        // 如果日志数量过多，随机选择一部分返回
        if (operLogs.size() > 10) {
            // 随机选择10条日志
            Collections.shuffle(operLogs);
            operLogs = operLogs.subList(0, 10);
            // 按时间重新排序
            operLogs.sort((a, b) -> b.getOperTime().compareTo(a.getOperTime()));
        }

        // 转换为DTO
        return operLogs.stream()
                .map(log -> OperLogDTO.builder()
                        .title(log.getTitle())
                        .content(log.getContent())
                        .operTime(log.getOperTime())
                        .method(log.getMethod())
                        .build())
                .collect(Collectors.toList());
    }  
}