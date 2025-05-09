package org.AList.domain.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.AList.domain.dao.entity.ContactDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 通讯信息持久层
 */
public interface ContactMapper extends BaseMapper<ContactDO> {
    @Select("SELECT * FROM t_student_contact WHERE student_id=#{contactId} AND del_flag=#{delFlag}")
    ContactDO selectSingleDeletedContact(
            @Param("contactId") String contactId,
            @Param("delFlag") Integer delFlag
    );

    // ContactMapper.java
    @Update("UPDATE t_student_contact SET del_flag = 0 WHERE student_id = #{contactId} AND del_flag = 1")
    int restoreContact(@Param("contactId") String contactId);
}
