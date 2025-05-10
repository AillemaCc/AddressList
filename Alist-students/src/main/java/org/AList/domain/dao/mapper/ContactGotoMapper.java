package org.AList.domain.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.AList.domain.dao.entity.ContactGotoDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 通讯信息路由表持久层
 */
public interface ContactGotoMapper extends BaseMapper<ContactGotoDO> {
    @Select("SELECT * FROM t_contact_goto WHERE contact_id=#{contactId} AND owner_id=#{ownerId} AND del_flag=#{delFlag}")
    ContactGotoDO selectSingleDeletedContactGoto(
            @Param("contactId") String contactId,
            @Param("ownerId") String ownerId,
            @Param("delFlag") Integer delFlag
    );

    @Update("UPDATE t_contact_goto SET del_flag = 0 WHERE contact_id = #{contactId} AND owner_id = #{ownerId} AND del_flag = 1")
    int restoreContactGoto(@Param("contactId") String contactId, @Param("ownerId") String ownerId);

    @Select("SELECT owner_id FROM t_contact_goto WHERE contact_id = #{contactId} AND del_flag = 0")
    List<String> selectOwnerIdsByContactId(@Param("contactId") String contactId);
}
