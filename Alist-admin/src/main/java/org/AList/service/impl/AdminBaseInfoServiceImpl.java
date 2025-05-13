package org.AList.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AList.common.convention.exception.ClientException;
import org.AList.domain.dao.entity.ClassInfoDO;
import org.AList.domain.dao.entity.ContactDO;
import org.AList.domain.dao.entity.StudentDO;
import org.AList.domain.dao.mapper.ClassInfoMapper;
import org.AList.domain.dao.mapper.ContactMapper;
import org.AList.domain.dao.mapper.StudentMapper;
import org.AList.domain.dto.req.BaseClassInfoAddReqDTO;
import org.AList.domain.dto.req.BaseClassInfoListStuReqDTO;
import org.AList.domain.dto.req.BaseClassInfoUpdateReqDTO;
import org.AList.domain.dto.req.BaseMajorInfoListClassReqDTO;
import org.AList.domain.dto.resp.BaseClassInfoListStuRespDTO;
import org.AList.domain.dto.resp.BaseMajorInfoListClassRespDTO;
import org.AList.service.AdminBaseInfoService;
import org.AList.service.CacheService.BaseInfoCacheService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
/**
 * 班级基本信息服务实现类。
 * 提供班级信息的增删改查、学生列表展示等业务逻辑。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminBaseInfoServiceImpl implements AdminBaseInfoService {
    private final ClassInfoMapper classInfoMapper;
    private final StudentMapper studentMapper;
    private final ContactMapper contactMapper;
    private final BaseInfoCacheService baseInfoCacheService;
    /**
     * 新增班级基本信息。
     *
     * @param requestParam 请求参数，包含班级编号和名称
     * @throws ClientException 如果请求参数为空、班级编号或名称为空、或班级已存在时抛出异常
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
     * 更新班级基本信息。
     *
     * @param requestParam 请求参数，包含班级编号和名称
     * @throws ClientException 如果请求参数为空、班级编号或名称为空、或班级不存在时抛出异常
     */
    @Override
    public void updateBaseClassInfo(BaseClassInfoUpdateReqDTO requestParam) {
        Objects.requireNonNull(requestParam, "请求参数不能为空");

        if (requestParam.getClassNum() == null || StringUtils.isBlank(requestParam.getClassName())) {
            throw new ClientException("班级编号和名称不能为空");
        }

        LambdaQueryWrapper<ClassInfoDO> queryWrapper = Wrappers.lambdaQuery(ClassInfoDO.class)
                .eq(ClassInfoDO::getClassNum, requestParam.getClassNum())
                .eq(ClassInfoDO::getClassName, requestParam.getClassName())
                .eq(ClassInfoDO::getDelFlag, 0);

        ClassInfoDO existingDO = classInfoMapper.selectOne(queryWrapper);
        if (existingDO == null) {
            throw new ClientException("要更新的班级信息不存在");
        }

        ClassInfoDO updateDO = ClassInfoDO.builder()
                .className(requestParam.getClassName())
                .classNum(requestParam.getClassNum())
                .build();

        int affectedRows = classInfoMapper.updateById(updateDO);
        if (affectedRows != 1) {
            throw new ClientException("更新失败，请重试");
        }
        baseInfoCacheService.clearStudentContactCacheByClass(requestParam.getClassNum());
        baseInfoCacheService.evictPageCacheByClass(requestParam.getClassNum());

    }
    /**
     * 分页查询某个班级的学生信息，并尝试使用缓存加速访问。
     *
     * @param requestParam 包含班级编号、当前页码和页面大小的请求参数
     * @return 学生信息分页结果
     * @throws ClientException 如果请求参数为空或班级编号为空时抛出异常
     */
    @Override
    public IPage<BaseClassInfoListStuRespDTO> listClassStu(BaseClassInfoListStuReqDTO requestParam) {
        if (requestParam == null || requestParam.getClassNum() == null) {
            throw new ClientException("请求参数或班级编号不能为空");
        }

        Integer classNum = requestParam.getClassNum();
        Integer current = requestParam.getCurrent() == null ? 1 : requestParam.getCurrent();
        Integer size = requestParam.getSize() == null ? 10 : requestParam.getSize();

        // 尝试从缓存中获取数据
        IPage<BaseClassInfoListStuRespDTO> cachedPage = baseInfoCacheService.getPageCacheByClass(classNum, current, size);
        if (cachedPage != null) {
            return cachedPage;
        }

        // 如果缓存未命中，则查询数据库
        IPage<BaseClassInfoListStuRespDTO> page = queryStudentsFromCacheAndDatabase(requestParam);

        // 查询成功后将结果写入缓存
        baseInfoCacheService.putPageCacheByClass(classNum, current, size, page);

        return page;
    }
    /**
     * 根据专业编号分页查询该专业下的所有班级信息。
     *
     * @param requestParam 请求参数，包含专业编号
     * @return 班级信息分页结果
     * @throws ClientException 如果请求参数为空或专业编号为空时抛出异常
     */
    @Override
    public IPage<BaseMajorInfoListClassRespDTO> listMajorClass(BaseMajorInfoListClassReqDTO requestParam) {
        if (requestParam == null || requestParam.getMajorNum() == null) {
            throw new ClientException("请求参数或专业编号不能为空");
        }
        Page<ClassInfoDO> page = new Page<>(1, 10);
        Wrapper<ClassInfoDO> queryWrapper = Wrappers.lambdaQuery(ClassInfoDO.class)
                .eq(ClassInfoDO::getMajorNum, requestParam.getMajorNum())
                .eq(ClassInfoDO::getDelFlag, 0)
                .orderByDesc(ClassInfoDO::getClassNum);
        IPage<ClassInfoDO> classInfoPage = classInfoMapper.selectPage(page, queryWrapper);

        return classInfoPage.convert(classInfoDO -> {
            BaseMajorInfoListClassRespDTO respDTO = new BaseMajorInfoListClassRespDTO();
            BeanUtils.copyProperties(classInfoDO, respDTO);
            return respDTO;
        });
    }
    /**
     * 查询某个班级的学生列表（结合缓存）。
     * 若缓存中没有学生通讯信息，则回退到数据库查询并写入缓存。
     *
     * @param requestParam 包含班级编号、当前页码和页面大小的请求参数
     * @return 学生信息分页结果
     */
    private IPage<BaseClassInfoListStuRespDTO> queryStudentsFromCacheAndDatabase(BaseClassInfoListStuReqDTO requestParam) {
        int current = requestParam.getCurrent()==null?1:requestParam.getCurrent();
        int size = requestParam.getSize()==null?10:requestParam.getSize();
        Page<StudentDO> page = new Page<>(current, size);

        LambdaQueryWrapper<StudentDO> queryWrapper = Wrappers.lambdaQuery(StudentDO.class)
                .eq(StudentDO::getClassName, requestParam.getClassNum())
                .eq(StudentDO::getDelFlag, 0)
                .orderByAsc(StudentDO::getStudentId);

        IPage<StudentDO> studentPage = studentMapper.selectPage(page, queryWrapper);

        return studentPage.convert(student -> {
            String studentId = student.getStudentId();
            // 尝试从缓存中获取学生的完整通讯信息
            BaseClassInfoListStuRespDTO respDTO = baseInfoCacheService.getFullContactInfoFromCache(studentId);
            if (respDTO == null) { // 缓存未命中，则构建响应并缓存
                ContactDO contact = contactMapper.selectOne(Wrappers.lambdaQuery(ContactDO.class)
                        .eq(ContactDO::getStudentId, studentId)
                        .eq(ContactDO::getDelFlag, 0));
                respDTO = buildFullResponse(student, contact);
                // 将新的完整通讯信息存入缓存
                baseInfoCacheService.putCacheStuFullContactInfo(studentId, respDTO);
            }
            return respDTO;
        });
    }
    /**
     * 构建完整的学生信息响应对象。
     *
     * @param student 学生实体对象
     * @param contact 联系人实体对象（可为null）
     * @return 包含学生和联系信息的响应DTO
     */
    private BaseClassInfoListStuRespDTO buildFullResponse(StudentDO student, ContactDO contact) {
        return BaseClassInfoListStuRespDTO.builder()
                .studentId(student.getStudentId())
                .name(student.getName())
                .enrollmentYear(student.getEnrollmentYear())
                .graduationYear(student.getGraduationYear())
                .phone(student.getPhone())
                .email(student.getEmail())
                .employer(contact != null ? contact.getEmployer() : null)
                .city(contact != null ? contact.getCity() : null)
                .build();
    }
}
