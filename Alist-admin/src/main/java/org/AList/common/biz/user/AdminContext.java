package org.AList.common.biz.user;
import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Optional;

/**
 * 管理员username上下文
 * 在同一个线程（或跨线程）中安全地存储和传递username信息，它基于 TransmittableThreadLocal（TTL）
 * 也是一个可选方案
 */
public class AdminContext {
    /**
     * <a href="https://github.com/alibaba/transmittable-thread-local" />
     */
    private static final ThreadLocal<AdminInfoDTO> USER_THREAD_LOCAL = new TransmittableThreadLocal<>();

    /**
     * 设置username至上下文
     *
     * @param username 用户详情信息
     */
    public static void setUsername(AdminInfoDTO username) {
        USER_THREAD_LOCAL.set(username);
    }

    /**
     * 获取username上下文
     * @return username 上下文当中的username
     */
    public static String getAdminister() {
        AdminInfoDTO stuIdInfoDTO =USER_THREAD_LOCAL.get();
        return Optional.ofNullable(stuIdInfoDTO).map(AdminInfoDTO::getUsername).orElse(null);
    }

    /**
     * 清理username上下文
     */
    public static void removeUsername() {
        USER_THREAD_LOCAL.remove();
    }
}
