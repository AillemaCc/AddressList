package org.AList.common.biz.user;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.AList.common.convention.exception.ClientException;

import java.util.Optional;

import static org.AList.common.convention.errorcode.BaseErrorCode.PERM_EDIT_USER_DENY;

/**
 * 用户学号上下文
 * 在同一个线程（或跨线程）中安全地存储和传递学生ID信息，它基于 TransmittableThreadLocal（TTL）
 * 也是一个可选方案
 */
@Slf4j
public class StuIdContext {
    /**
     * <a href="https://github.com/alibaba/transmittable-thread-local" />
     */
    private static final ThreadLocal<StuIdInfoDTO> USER_THREAD_LOCAL = new TransmittableThreadLocal<>();

    /**
     * 设置学号至上下文
     *
     * @param studentId 用户详情信息
     */
    public static void setStudentId(StuIdInfoDTO studentId) {
        USER_THREAD_LOCAL.set(studentId);
    }

    /**
     * 获取学号上下文
     * @return 学号
     */
    public static String getStudentId() {
        StuIdInfoDTO stuIdInfoDTO =USER_THREAD_LOCAL.get();
        return Optional.ofNullable(stuIdInfoDTO).map(StuIdInfoDTO::getStudentId).orElse(null);
    }

    /**
     * 清理学号上下文
     */
    public static void removeStudentId() {
        USER_THREAD_LOCAL.remove();
    }

    /**
     * get上下文的学号
     */
    public static StuIdInfoDTO getStudentIdDTO(){
        return USER_THREAD_LOCAL.get();
    }



    /**
     * 验证当前用户是否为登录用户，也就是修改操作的鉴权
     */
    public static void verifyLoginUser(String targetStudentId) {
        StuIdInfoDTO currentStu = getStudentIdDTO();
        String currentStudentId = currentStu != null ? currentStu.getStudentId() : null;

        // 打印调用堆栈信息（可选）
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String callerClass = "Unknown";
        String callerMethod = "Unknown";

        for (StackTraceElement element : stackTrace) {
            if (!element.getClassName().equals(StuIdContext.class.getName()) &&
                    !element.getClassName().contains("java.lang.Thread")) {
                callerClass = element.getClassName();
                callerMethod = element.getMethodName();
                break;
            }
        }

        log.info("【鉴权开始】");
        log.info("调用位置: {}.{}", callerClass, callerMethod);
        log.info("期望操作的学号: {}", targetStudentId);
        log.info("当前上下文学号: {}", currentStudentId);
        log.info("当前线程名: {}", Thread.currentThread().getName());

        if (currentStu == null) {
            log.warn("❌ 当前用户未登录（上下文为空）");
            throw new ClientException(PERM_EDIT_USER_DENY);
        }

        if (!currentStu.getStudentId().equals(targetStudentId)) {
            log.warn("❌ 权限不足：当前学号 {} 无法操作目标学号 {}", currentStudentId, targetStudentId);
            throw new ClientException(PERM_EDIT_USER_DENY);
        }

        log.info("✅ 鉴权通过：当前学号 {} 成功操作目标学号 {}", currentStudentId, targetStudentId);
        log.info("【鉴权结束】");
    }
}

