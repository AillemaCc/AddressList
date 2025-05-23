package org.AList.common.scheduler;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AList.domain.dao.entity.ApplicationDO;
import org.AList.domain.dao.mapper.ApplicationMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApplicationCleanupScheduler {

    private final ApplicationMapper applicationMapper;

    /**
     * 每天凌晨2点执行清理任务
     * 对过期的待审核申请自动拒绝，而不是删除
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredApplications() {
        log.info("开始执行过期申请清理任务");

        try {
            // 自动拒绝超过30天的待审核申请
            int autoRejected = autoRejectExpiredApplications(30);

            // 清理超过180天的已处理申请（保留更长时间）
            int deleted = cleanupOldProcessedApplications(180);

            log.info("申请清理任务完成，自动拒绝过期申请: {}条，清理旧申请: {}条",
                    autoRejected, deleted);
        } catch (Exception e) {
            log.error("申请清理任务执行失败", e);
        }
    }

    /**
     * 自动拒绝过期的待审核申请
     */
    private int autoRejectExpiredApplications(int days) {
        LocalDateTime expireTime = LocalDateTime.now().minusDays(days);
        LambdaUpdateWrapper<ApplicationDO> updateWrapper = Wrappers.lambdaUpdate(ApplicationDO.class)
                .eq(ApplicationDO::getStatus, 0)  // 待审核状态
                .eq(ApplicationDO::getDelFlag, 0)
                .lt(ApplicationDO::getCreateTime, expireTime)
                .set(ApplicationDO::getContent, "申请已过期，系统自动拒绝")
                .set(ApplicationDO::getStatus, 2);  // 设置为拒绝状态，而不是删除
        return applicationMapper.update(null, updateWrapper);
    }

    /**
     * 清理很久以前的已处理申请
     */
    private int cleanupOldProcessedApplications(int days) {
        LocalDateTime expireTime = LocalDateTime.now().minusDays(days);
        LambdaUpdateWrapper<ApplicationDO> updateWrapper = Wrappers.lambdaUpdate(ApplicationDO.class)
                .in(ApplicationDO::getStatus, Arrays.asList(1, 2))  // 已通过或已拒绝
                .eq(ApplicationDO::getDelFlag, 0)
                .lt(ApplicationDO::getCreateTime, expireTime)
                .set(ApplicationDO::getDelFlag, 1);

        return applicationMapper.update(null, updateWrapper);
    }
}