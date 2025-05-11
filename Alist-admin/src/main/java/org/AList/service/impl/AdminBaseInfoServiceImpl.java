package org.AList.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.AList.common.convention.exception.ClientException;
import org.AList.domain.dao.entity.ClassInfoDO;
import org.AList.domain.dao.entity.ContactDO;
import org.AList.domain.dao.entity.ContactGotoDO;
import org.AList.domain.dao.entity.StudentDO;
import org.AList.domain.dao.mapper.ClassInfoMapper;
import org.AList.domain.dao.mapper.ContactGotoMapper;
import org.AList.domain.dao.mapper.ContactMapper;
import org.AList.domain.dao.mapper.StudentMapper;
import org.AList.domain.dto.req.BaseClassInfoAddReqDTO;
import org.AList.domain.dto.req.BaseClassInfoListStuReqDTO;
import org.AList.domain.dto.req.BaseClassInfoUpdateReqDTO;
import org.AList.domain.dto.resp.BaseClassInfoListStuRespDTO;
import org.AList.service.AdminBaseInfoService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 管理员基础信息操作服务接口实现层
 */
@Service
@RequiredArgsConstructor
public class AdminBaseInfoServiceImpl implements AdminBaseInfoService {
    private final ClassInfoMapper classInfoMapper;
    private final StudentMapper studentMapper;
    private final ContactGotoMapper contactGotoMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ContactMapper contactMapper;
    /**
     * 新增班级基础信息
     *
     * @param requestParam 新增班级信息请求体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBaseClassInfo(BaseClassInfoAddReqDTO requestParam) {
        Objects.requireNonNull(requestParam, "请求参数不能为空");
        if (requestParam.getClassNum() == null || StringUtils.isBlank(requestParam.getClassName())) {
            throw new ClientException("班级编号和名称不能为空");
        }
        Integer classNum = requestParam.getClassNum();
        String className = requestParam.getClassName();
        LambdaQueryWrapper<ClassInfoDO> uniqueWrapper = Wrappers.lambdaQuery(ClassInfoDO.class)
                .eq(ClassInfoDO::getClassNum, classNum)
                .eq(ClassInfoDO::getClassName, className)
                .eq(ClassInfoDO::getDelFlag, 0);
        ClassInfoDO uniqueDO = classInfoMapper.selectOne(uniqueWrapper);
        if (uniqueDO != null) {
            throw new ClientException("新增的班级信息已存在，请不要重复添加");
        }
        ClassInfoDO classInfoDO =ClassInfoDO.builder()
                .className(className)
                .classNum(classNum)
                .build();
        int insert = classInfoMapper.insert(classInfoDO);
        if (insert!=1){
            throw new ClientException("新增异常，请重试");
        }
    }

    /**
     * 更新班级基础信息
     *
     * @param requestParam 更新班级信息请求体
     */
    @Override
    public void updateBaseClassInfo(BaseClassInfoUpdateReqDTO requestParam) {
        // 参数校验
        Objects.requireNonNull(requestParam, "请求参数不能为空");

        if (requestParam.getClassNum() == null || StringUtils.isBlank(requestParam.getClassName())) {
            throw new ClientException("班级编号和名称不能为空");
        }

        // 查询现有记录
        LambdaQueryWrapper<ClassInfoDO> queryWrapper = Wrappers.lambdaQuery(ClassInfoDO.class)
                .eq(ClassInfoDO::getClassNum, requestParam.getClassNum())
                .eq(ClassInfoDO::getClassName, requestParam.getClassName())
                .eq(ClassInfoDO::getDelFlag, 0);

        ClassInfoDO existingDO = classInfoMapper.selectOne(queryWrapper);
        if (existingDO == null) {
            throw new ClientException("要更新的班级信息不存在");
        }

        // 构建更新对象
        ClassInfoDO updateDO = ClassInfoDO.builder()
                .id(existingDO.getId()) // 使用查询到的自增ID
                .className(requestParam.getClassName())
                .classNum(requestParam.getClassNum())
                // 可以添加其他需要更新的字段
                .build();

        // 执行更新
        int affectedRows = classInfoMapper.updateById(updateDO);
        if (affectedRows != 1) {
            throw new ClientException("更新失败，请重试");
        }
        // 清理关联学生的通讯录缓存
        clearStudentContactCacheByClass(existingDO.getClassNum());

    }

    /**
     * 分页展示某个班级下的学生信息
     *
     * @param requestParam 查询班级下面的学生请求体
     * @return 分页响应
     */
    @Override
    public IPage<BaseClassInfoListStuRespDTO> listClassStu(BaseClassInfoListStuReqDTO requestParam) {
        // 参数校验
        if (requestParam == null || requestParam.getClassNum() == null) {
            throw new ClientException("请求参数或班级编号不能为空");
        }

        // 创建分页对象
        Page<StudentDO> page = new Page<>(1, 10);

        // 查询学生基本信息（分页）
        LambdaQueryWrapper<StudentDO> studentQueryWrapper = Wrappers.lambdaQuery(StudentDO.class)
                .eq(StudentDO::getClassName, requestParam.getClassNum())
                .eq(StudentDO::getDelFlag, 0)
                .orderByAsc(StudentDO::getStudentId); // 按学号排序

        IPage<StudentDO> studentPage = studentMapper.selectPage(page, studentQueryWrapper);

        // 转换为响应DTO
        return studentPage.convert(student -> {
            // 查询通讯录信息
            ContactDO contact = contactMapper.selectOne(Wrappers.lambdaQuery(ContactDO.class)
                    .eq(ContactDO::getStudentId, student.getStudentId())
                    .eq(ContactDO::getDelFlag, 0));

            if (contact == null) {
                throw new ClientException("学生ID为 " + student.getStudentId() + " 的通讯录信息不存在");
            }

            // 构建响应DTO
            return BaseClassInfoListStuRespDTO.builder()
                    .studentId(student.getStudentId())
                    .name(student.getName())
                    .enrollmentYear(student.getEnrollmentYear())
                    .graduationYear(student.getGraduationYear())
                    .phone(student.getPhone())
                    .email(student.getEmail())
                    .employer(contact.getEmployer())
                    .city(contact.getCity())
                    .build();
        });
    }


    /**
     * 重建缓存保证数据一致性
     * @param classNum 班级代码
     */
    private void clearStudentContactCacheByClass(Integer classNum) {
        // 1. 查询该班级下的所有学生
        LambdaQueryWrapper<StudentDO> studentQueryWrapper = Wrappers.lambdaQuery(StudentDO.class)
                .eq(StudentDO::getClassName, classNum)
                .eq(StudentDO::getDelFlag, 0);

        List<StudentDO> students = studentMapper.selectList(studentQueryWrapper);
        if (CollectionUtils.isEmpty(students)) {
            return;
        }

        // 2. 为每个学生清理通讯录缓存
        students.forEach(student -> {
            // 查询所有拥有该学生通讯录的用户
            LambdaQueryWrapper<ContactGotoDO> gotoQueryWrapper = Wrappers.lambdaQuery(ContactGotoDO.class)
                    .eq(ContactGotoDO::getContactId, student.getStudentId())
                    .eq(ContactGotoDO::getDelFlag, 0);

            List<ContactGotoDO> contactGotos = contactGotoMapper.selectList(gotoQueryWrapper);

            // 删除所有相关的缓存键
            contactGotos.forEach(gotoRecord -> {
                String redisKey = String.format("contact:%s:%s",
                        gotoRecord.getOwnerId(),
                        gotoRecord.getContactId());
                stringRedisTemplate.delete(redisKey);
            });
        });
    }
}
