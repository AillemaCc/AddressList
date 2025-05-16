package org.AList.common.generator;

import org.AList.common.constant.RedisKeyTemplates;

/**
 * Redis Key 生成工具
 */
public class RedisKeyGenerator {
    
    /**
     * 生成联系人单条缓存 Key
     */
    public static String genContactKey(String ownerId, String contactId) {
        return String.format(RedisKeyTemplates.CONTACT_SINGLE, ownerId, contactId);
    }
    
    /**
     * 生成学生所有联系人缓存匹配模式
     */
    public static String genContactPatternForStudent(String studentId) {
        return String.format(RedisKeyTemplates.CONTACT_ALL_FOR_STUDENT, studentId);
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