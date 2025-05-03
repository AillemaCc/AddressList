package org.AList.domain.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.AList.domain.dao.entity.LoginLogDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 登录日志持久层
 */
public interface LoginLogMapper extends BaseMapper<LoginLogDO> {
    // 查询某学生的最后一条登录记录
    @Select("SELECT * FROM t_student_login_log WHERE student_id = #{studentId} ORDER BY update_time DESC LIMIT 1")
    LoginLogDO selectLastLoginByStudentId(@Param("studentId") String studentId);

    // 移除自定义的 updateById 方法，直接使用 BaseMapper 提供的
    // 新增专门更新登录次数的方法
    @Update("UPDATE t_student_login_log SET " +
            "ip = #{ip}, os = #{os}, browser = #{browser}, " +
            "update_time = #{updateTime}, frequency = #{frequency} " +
            "WHERE student_id = #{studentId}")
    int updateLoginLog(LoginLogDO loginLogDO);
}
