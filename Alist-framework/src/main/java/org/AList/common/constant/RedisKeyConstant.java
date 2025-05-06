package org.AList.common.constant;
/**
 * redis常量
 */
public class RedisKeyConstant {
    /**
     * 管理员禁用用户锁前缀Key
     */
    public static final String LOCK_UPDATE_BAN_KEY = "lock:admin:update:ban:%s";

    /**
     * 管理员解除用户禁用锁前缀key
     */
    public static final String LOCK_UPDATE_UNBAN_KEY = "lock:admin:update:unban:%s";
}
