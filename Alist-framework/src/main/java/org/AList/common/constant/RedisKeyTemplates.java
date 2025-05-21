package org.AList.common.constant;

/**
 * Redis Key 模板常量（统一管理所有 Key 格式）
 */
public final class RedisKeyTemplates {
    
    // 联系人相关
    public static final String CONTACT_SINGLE = "contact:%s:%s";
    public static final String CONTACT_NULL_SINGLE = "contact:%s:%s";
    public static final String CONTACT_ALL_FOR_STUDENT = "contact:*:%s";

    public static final String STUDENT_LOGIN_ACCESS = "login:student:%s";
    public static final String STUDENT_LOGIN_REFRESH = "refresh:student:%s";
    public static final String STUDENT_LOGIN_INFO = "login:student:info:%s";

    public static final String STUDENT_FULL_CONTACT_INFO = "student:fullContactInfo:%s";
    public static final String CLASSPAGE_STUDENTS_FULL_CONTACT_INFO = "classPage:students:%s:%d:%d";
    // 管理员操作锁
    public static final String LOCK_ADMIN_UPDATE_BAN = "lock:admin:update:ban:%s";
    public static final String LOCK_ADMIN_UPDATE_UNBAN = "lock:admin:update:unban:%s";

    public static final String LOCK_STUDENT_REGISTER_KEY="AddressList:lock:student:register:%s";
    
    // 禁止实例化
    private RedisKeyTemplates() {}
}