package org.AList.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.AList.common.biz.user.AdminContext;
import org.AList.common.convention.exception.ClientException;
import org.AList.domain.dao.entity.OperLogDO;
import org.AList.domain.dao.mapper.OperLogMapper;
import org.AList.domain.dto.req.AdminHomePageReqDTO;
import org.AList.domain.dto.req.AuditListReqDTO;
import org.AList.domain.dto.req.BoardQueryReqDTO;
import org.AList.domain.dto.resp.*;
import org.AList.service.AdminHomePageService;
import org.AList.service.AdministerAuditService;
import org.AList.service.BoardService;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.AList.common.convention.errorcode.BaseErrorCode.ADMIN_INFO_UNAVAILABLE;

// Service实现
@Service
@RequiredArgsConstructor
public class AdminHomePageServiceImpl implements AdminHomePageService {
    private final AdministerAuditService administerAuditService;
    private final BoardService boardService;
    private final OperLogMapper operLogMapper;
      
    @Override  
    public AdminHomePageDataDTO getAdminHomePageData(AdminHomePageReqDTO requestParam) {
        // 1. 获取用户名
        String username = requestParam.getUsername();
        if (username == null || username.trim().isEmpty()) {
            username = AdminContext.getAdminister();
            if (username == null) {
                throw new ClientException(ADMIN_INFO_UNAVAILABLE);                                                      //"B0202", "系统无法获取当前管理员信息"
            }
        }

        // 2. 获取公告统计数据
        BoardDTO boardStats = getBoardStatistics();

        // 3. 获取请求统计数据
        RequestDTO requestStats = getRequestStatistics();

        // 4. 构建并返回结果
        return AdminHomePageDataDTO.builder()
                .username(username)
                .board(boardStats)
                .request(requestStats)
                .build();
    }

    /**
     * 获取公告统计数据
     */
    private BoardDTO getBoardStatistics() {
        // 获取草稿数量
        Integer draftCount = getDraftBoardCount();

        // 获取已发布数量
        Integer releasedCount = getReleasedBoardCount();

        // 获取已下架数量
        Integer pulledOffCount = getPulledOffBoardCount();

        return BoardDTO.builder()
                .draft(draftCount)
                .released(releasedCount)
                .pulledoff(pulledOffCount)
                .build();
    }

    /**
     * 获取请求统计数据
     */
    private RequestDTO getRequestStatistics() {
        // 获取待审核数量
        Integer pendingCount = getPendingAuditCount();

        // 获取已通过数量
        Integer approvedCount = getApprovedAuditCount();

        // 获取已拒绝数量
        Integer rejectedCount = getRejectedAuditCount();

        return RequestDTO.builder()
                .pending(pendingCount)
                .approved(approvedCount)
                .rejected(rejectedCount)
                .build();
    }

    private Integer getPendingAuditCount() {
        AuditListReqDTO requestParam = new AuditListReqDTO();
        requestParam.setCurrent(1);
        requestParam.setSize(1);
        IPage<AuditUserPageRespDTO> auditPage = administerAuditService.listAuditRegister(requestParam);
        return Math.toIntExact(auditPage.getTotal());
    }

    private Integer getApprovedAuditCount() {
        AuditListReqDTO requestParam = new AuditListReqDTO();
        requestParam.setCurrent(1);
        requestParam.setSize(1);
        IPage<AuditUserPageRespDTO> auditPage = administerAuditService.listAuditRegisterValid(requestParam);
        return Math.toIntExact(auditPage.getTotal());
    }

    private Integer getRejectedAuditCount() {
        AuditListReqDTO requestParam = new AuditListReqDTO();
        requestParam.setCurrent(1);
        requestParam.setSize(1);
        IPage<AuditUserPageRespDTO> auditPage = administerAuditService.listAuditRegisterRefuse(requestParam);
        return Math.toIntExact(auditPage.getTotal());
    }

    private Integer getReleasedBoardCount() {
        BoardQueryReqDTO requestParam = new BoardQueryReqDTO();
        requestParam.setCurrent(1);
        requestParam.setSize(1);
        IPage<BoardQueryRespDTO> boardPage = boardService.queryAllReleased(requestParam);
        return Math.toIntExact(boardPage.getTotal());
    }

    private Integer getDraftBoardCount() {
        BoardQueryReqDTO requestParam = new BoardQueryReqDTO();
        requestParam.setCurrent(1);
        requestParam.setSize(1);
        IPage<BoardQueryRespDTO> boardPage = boardService.queryAllDraft(requestParam);
        return Math.toIntExact(boardPage.getTotal());
    }

    private Integer getPulledOffBoardCount() {
        BoardQueryReqDTO requestParam = new BoardQueryReqDTO();
        requestParam.setCurrent(1);
        requestParam.setSize(1);
        IPage<BoardQueryRespDTO> boardPage = boardService.queryAllPullOff(requestParam);
        return Math.toIntExact(boardPage.getTotal());
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