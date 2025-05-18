package org.AList.domain.dao.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.AList.domain.dao.entity.StudentDefaultInfoDO;
import org.AList.domain.dto.req.ExeclStudentExportConditionReqDTO;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 学生学籍信息持久层
 */
public interface StudentDefaultInfoMapper extends BaseMapper<StudentDefaultInfoDO>{
    /**
     * 选取所有学生ID
     * @return 学生ID列表
     */
    default List<String> selectAllStudentIds() {
        QueryWrapper<StudentDefaultInfoDO> wrapper = new QueryWrapper<>();
        wrapper.select("student_id");
        return selectObjs(wrapper).stream()
                .map(obj -> (String) obj)
                .collect(Collectors.toList());
    }

    /**
     * 条件查询学籍信息
     */
    @Select("<script>" +
            "SELECT * FROM t_student_definfo " +
            "<where>" +
            "   <if test='majorNum != null'> AND major_num = #{majorNum} </if>" +
            "   <if test='classNum != null'> AND class_num = #{classNum} </if>" +
            "   <if test='enrollmentYear != null'> AND enrollment_year = #{enrollmentYear} </if>" +
            "   <if test='graduationYear != null'> AND graduation_year = #{graduationYear} </if>" +
            "</where>" +
            "</script>")
    List<StudentDefaultInfoDO> selectByCondition(ExeclStudentExportConditionReqDTO condition);
}
