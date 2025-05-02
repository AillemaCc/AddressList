package org.AList.common.biz.user;
import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Optional;

/**
 * 用户学号上下文
 */
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
}
