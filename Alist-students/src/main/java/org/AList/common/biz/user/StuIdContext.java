package org.AList.common.biz.user;
import com.alibaba.ttl.TransmittableThreadLocal;
import org.AList.common.convention.exception.ClientException;
import static org.AList.common.convention.errorcode.BaseErrorCode.*;
import java.util.Optional;

/**
 * 用户学号上下文
 * 在同一个线程（或跨线程）中安全地存储和传递学生ID信息，它基于 TransmittableThreadLocal（TTL）
 * 也是一个可选方案
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

    /**
     * get上下文的学号
     */
    public static StuIdInfoDTO getStudentIdDTO(){
        return USER_THREAD_LOCAL.get();
    }



    /**
     * 验证当前用户是否为登录用户，也就是修改操作的鉴权
     */
    public static void verifyLoginUser(String studentId) {
        StuIdInfoDTO currentStu = getStudentIdDTO();
        if(currentStu == null||!currentStu.getStudentId().equals(studentId)){
            throw new ClientException(PERM_EDIT_USER_DENY);                                                         //B0321：系统缺乏权限修改现用户信息
        }

    }
}
