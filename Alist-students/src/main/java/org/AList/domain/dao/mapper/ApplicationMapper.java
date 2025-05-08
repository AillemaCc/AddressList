package org.AList.domain.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.AList.domain.dao.entity.ApplicationDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 站内信持久层
 */
public interface ApplicationMapper extends BaseMapper<ApplicationDO> {
    @Select("SELECT * FROM t_contact_application WHERE receiver = #{receiver} AND del_flag = #{delFlag}")
    IPage<ApplicationDO> selectDeletedApplications(
            Page<ApplicationDO> page,
            @Param("receiver") String receiver,
            @Param("delFlag") Integer delFlag
    );
}

