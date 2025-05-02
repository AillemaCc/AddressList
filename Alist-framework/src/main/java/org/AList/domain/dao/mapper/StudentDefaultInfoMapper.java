package org.AList.domain.dao.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.AList.domain.dao.entity.StudentDefaultInfoDO;

import java.util.List;
import java.util.stream.Collectors;

public interface StudentDefaultInfoMapper extends BaseMapper<StudentDefaultInfoDO>{
    // 在 StudentMapper.java 接口中添加
    default List<String> selectAllStudentIds() {
        QueryWrapper<StudentDefaultInfoDO> wrapper = new QueryWrapper<>();
        wrapper.select("student_id");
        return selectObjs(wrapper).stream()
                .map(obj -> (String) obj)
                .collect(Collectors.toList());
    }
}
