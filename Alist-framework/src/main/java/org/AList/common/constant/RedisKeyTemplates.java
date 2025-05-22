package org.AList.common.constant;

/**
 * Redis Key 模板常量（统一管理所有 Key 格式）
 */
public final class RedisKeyTemplates {
    

    public static final String CONTACT_SINGLE = "contact:%s:%s";
    public static final String CONTACT_NULL_SINGLE = "contact:%s:%s";
    public static final String CONTACT_ALL_FOR_STUDENT = "contact:*:%s";

    public static final String STUDENT_LOGIN_ACCESS = "login:student:%s";
    public static final String STUDENT_LOGIN_REFRESH = "refresh:student:%s";
    public static final String STUDENT_LOGIN_INFO = "login:student:info:%s";
    public static final String ADMIN_LOGIN_ACCESS = "login:administer:%s";
    public static final String ADMIN_LOGIN_REFRESH = "refresh:administer:%s";
    public static final String ADMIN_LOGIN_INFO = "login:administer:info:%s";

    public static final String STUDENT_FULL_CONTACT_INFO = "student:fullContactInfo:%s";
    public static final String CLASSPAGE_STUDENTS_FULL_CONTACT_INFO = "classPage:students:%s:%d:%d";

    public static final String LOCK_ADMIN_UPDATE_BAN = "lock:admin:update:ban:%s";
    public static final String LOCK_ADMIN_UPDATE_UNBAN = "lock:admin:update:unban:%s";

    public static final String LOCK_STUDENT_REGISTER_KEY="AddressList:lock:student:register:%s";
    

    private RedisKeyTemplates() {}
}