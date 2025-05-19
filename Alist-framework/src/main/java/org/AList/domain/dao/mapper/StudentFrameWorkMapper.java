package org.AList.domain.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.AList.domain.dao.entity.StudentFrameworkDO;
import org.AList.domain.dto.req.QuerySomeoneReqDTO;
import org.AList.domain.dto.resp.QuerySomeoneRespDTO;
import org.apache.ibatis.annotations.Select;

/**
 * 学生实体类持久层
 */
public interface StudentFrameWorkMapper extends BaseMapper<StudentFrameworkDO> {
    /**
     * 条件查询学籍信息
     */
    @Select("<script>" +
            "SELECT * FROM t_student_definfo " +
            "<where>" +
            "   <if test='name != null'> AND name = #{name} </if>" +
            "   <if test='majorNum != null'> AND major_num = #{majorNum} </if>" +
            "   <if test='classNum != null'> AND class_num = #{classNum} </if>" +
            "</where>" +
            "</script>")
    IPage<QuerySomeoneRespDTO> selectByCondition(QuerySomeoneReqDTO requestParam);
}
