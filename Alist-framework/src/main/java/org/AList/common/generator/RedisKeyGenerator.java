package org.AList.common.generator;

import org.AList.common.constant.RedisKeyTemplates;

/**
 * Redis Key 生成工具
 */
public class RedisKeyGenerator {
    
    /**
     * 生成联系人单条缓存 Key
     * <p>
     * contact:%s:%s
     */
    public static String genContactKey(String ownerId, String contactId) {
        return String.format(RedisKeyTemplates.CONTACT_SINGLE, ownerId, contactId);
    }

    /**
     * 生成联系人单条缓存空 Key
     * <p>
     * contactNull:%s:%s
     */
    public static String genContactNullKey(String ownerId, String contactId) {
        return String.format(RedisKeyTemplates.CONTACT_NULL_SINGLE, ownerId, contactId);
    }
    
    /**
     * 生成学生所有联系人缓存匹配模式
     */
    public static String genContactPatternForStudent(String studentId) {
        return String.format(RedisKeyTemplates.CONTACT_ALL_FOR_STUDENT, studentId);
    }

    /**
     * 生成登录AccessToken Key
     */
    public static String genStudentLoginAccess(String studentId) {
        return String.format(RedisKeyTemplates.STUDENT_LOGIN_ACCESS, studentId);
    }

    /**
     * 生成登录RefreshToken Key
     */
    public static String genStudentLoginRefresh(String studentId) {
        return String.format(RedisKeyTemplates.STUDENT_LOGIN_REFRESH, studentId);
    }

    /**
     * 生成登录之后的完整用户信息 Key
     */
    public static String genStudentLoginInfo(String studentId) {
        return String.format(RedisKeyTemplates.STUDENT_LOGIN_INFO, studentId);
    }

    /**
     * 生成单个学生信息缓存 Key
     */
    public static String genStudentFullContactInfo(String studentId) {
        return String.format(RedisKeyTemplates.STUDENT_FULL_CONTACT_INFO, studentId);
    }

    /**
     * 生成班级--学生信息分页查询缓存 Key
     */
    public static String genClassPageStudentsFullContactInfo(Integer classNum, Integer current, Integer size) {
        return String.format(RedisKeyTemplates.CLASSPAGE_STUDENTS_FULL_CONTACT_INFO, classNum, current != null ? current : 1, size != null ? size : 10);
    }

    /**
     * 生成管理员封禁用户锁 Key
     */
    public static String genAdminBanLockKey(String userId) {
        return String.format(RedisKeyTemplates.LOCK_ADMIN_UPDATE_BAN, userId);
    }

    /**
     * 生成管理员解禁用户锁 Key
     */
    public static String genAdminUnlockKey(String userId) {
        return String.format(RedisKeyTemplates.LOCK_ADMIN_UPDATE_UNBAN,userId);
    }

    /**
     * 生成用户注册学号分布式锁
     */
    public static String genStudentRegisterLockKey(String studentId) {
        return String.format(RedisKeyTemplates.LOCK_STUDENT_REGISTER_KEY,studentId);
    }
}