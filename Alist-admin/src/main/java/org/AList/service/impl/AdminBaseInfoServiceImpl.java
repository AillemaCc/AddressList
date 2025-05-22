package org.AList.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AList.common.convention.exception.ClientException;
import org.AList.domain.dao.entity.*;
import org.AList.domain.dao.mapper.*;
import org.AList.domain.dto.req.*;
import org.AList.domain.dto.resp.BaseAcademyInfoListMajorRespDTO;
import org.AList.domain.dto.resp.BaseClassInfoListStuRespDTO;
import org.AList.domain.dto.resp.BaseMajorInfoListClassRespDTO;
import org.AList.service.AdminBaseInfoService;
import org.AList.service.CacheService.BaseInfoCacheService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
    private final StudentFrameWorkMapper studentFrameWorkMapper;
    private final ContactMapper contactMapper;
    private final MajorAndAcademyMapper majorAndAcademyMapper;
    private final BaseInfoCacheService baseInfoCacheService;
    private final StudentDefaultInfoMapper studentDefaultInfoMapper;
    private final RegisterMapper registerMapper;
    private final LoginLogMapper loginLogMapper;

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
        Integer majorNum = requestParam.getMajorNum();
        String majorName = requestParam.getMajorName();
        Integer academyNum = requestParam.getAcademyNum();
        String academyName = requestParam.getAcademyName();
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
                .majorNum(majorNum)
                .build();
        int insert = classInfoMapper.insert(classInfoDO);
        if (insert!=1){
            throw new ClientException("新增异常，请重试");
        }
        // 处理专业与学院信息
        if (majorNum != null && academyNum != null) {
            LambdaQueryWrapper<MajorAndAcademyDO> queryWrapper = Wrappers.lambdaQuery(MajorAndAcademyDO.class)
                    .eq(MajorAndAcademyDO::getMajorNum, majorNum)
                    .eq(MajorAndAcademyDO::getAcademyNum, academyNum)
                    .eq(MajorAndAcademyDO::getDelFlag, 0);

            MajorAndAcademyDO existingMajorAcademy = majorAndAcademyMapper.selectOne(queryWrapper);

            MajorAndAcademyDO majorAndAcademyDO = MajorAndAcademyDO.builder()
                    .major(majorName)
                    .majorNum(majorNum)
                    .academy(academyName)
                    .academyNum(academyNum)
                    .build();

            if (existingMajorAcademy != null) {
                // 更新现有记录
                int updateResult = majorAndAcademyMapper.updateById(majorAndAcademyDO);
                if (updateResult != 1) {
                    throw new ClientException("更新专业与学院信息失败，请重试");
                }
            } else {
                // 插入新记录
                int insertResult = majorAndAcademyMapper.insert(majorAndAcademyDO);
                if (insertResult != 1) {
                    throw new ClientException("新增专业与学院信息失败，请重试");
                }
            }
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
        int current = requestParam.getCurrent()==null?1:requestParam.getCurrent();
        int size = requestParam.getSize()==null?10:requestParam.getSize();
        Page<ClassInfoDO> page = new Page<>(current, size);
        Wrapper<ClassInfoDO> queryWrapper = Wrappers.lambdaQuery(ClassInfoDO.class)
                .eq(ClassInfoDO::getMajorNum, requestParam.getMajorNum())
                .eq(ClassInfoDO::getDelFlag, 0)
                .orderByDesc(ClassInfoDO::getClassNum);
        IPage<ClassInfoDO> classInfoPage = classInfoMapper.selectPage(page, queryWrapper);

        // 获取专业和学院信息
        MajorAndAcademyDO majorAndAcademy = majorAndAcademyMapper.selectOne(Wrappers.lambdaQuery(MajorAndAcademyDO.class)
                .eq(MajorAndAcademyDO::getMajorNum, requestParam.getMajorNum())
                .eq(MajorAndAcademyDO::getDelFlag, 0));

        // 专业和学院信息
        final String majorName = majorAndAcademy != null ? majorAndAcademy.getMajor() : null;
        final String academyName = majorAndAcademy != null ? majorAndAcademy.getAcademy() : null;
        final Integer academyNum = majorAndAcademy != null ? majorAndAcademy.getAcademyNum() : null;

        return classInfoPage.convert(classInfoDO -> {
            BaseMajorInfoListClassRespDTO respDTO = new BaseMajorInfoListClassRespDTO();
            BeanUtils.copyProperties(classInfoDO, respDTO);

            // 设置新增字段
            respDTO.setMajorNum(requestParam.getMajorNum());
            respDTO.setMajor(majorName);
            respDTO.setAcademy(academyName);
            respDTO.setAcademyNum(academyNum);

            return respDTO;
        });
    }

    /**
     * 分页展示某个学院下的班级信息
     *
     * @param requestParam 查询学院下面的专业请求体
     * @return 分页相应
     */
    @Override
    public IPage<BaseAcademyInfoListMajorRespDTO> listAcademyMajor(BaseAcademyInfoListMajorReqDTO requestParam) {
        if(requestParam==null||requestParam.getAcademyNum()==null){
            throw new ClientException("请求参数或学院编号不能为空");
        }
        int current = requestParam.getCurrent()==null?1:requestParam.getCurrent();
        int size = requestParam.getSize()==null?10:requestParam.getSize();
        LambdaQueryWrapper<MajorAndAcademyDO> queryWrapper = Wrappers.lambdaQuery(MajorAndAcademyDO.class)
                .eq(MajorAndAcademyDO::getAcademyNum, requestParam.getAcademyNum())
                .eq(MajorAndAcademyDO::getDelFlag, 0);
        Page<MajorAndAcademyDO> page=new Page<>(current, size);
        IPage<MajorAndAcademyDO> majorPage=majorAndAcademyMapper.selectPage(page,queryWrapper);
        return majorPage.convert(major->{
            BaseAcademyInfoListMajorRespDTO responseDTO = new BaseAcademyInfoListMajorRespDTO();
            BeanUtils.copyProperties(major, responseDTO);
            return responseDTO;
        });
    }

    /**
     * 更新班级所属的专业和学院信息，包括班级信息的调整
     * 如果专业或班级信息变更，会同步更新相关学生表中的信息
     *
     * @param requestParam 请求参数
     */
    @Override
    public void updateBaseClassInfoMA(BaseClassInfoUpdateMAReqDTO requestParam) {
        // 参数校验
        if (requestParam == null || requestParam.getClassNum() == null) {
            throw new ClientException("请求参数或班级编号不能为空");
        }

        // 查询原始班级信息
        LambdaQueryWrapper<ClassInfoDO> classInfoWrapper = Wrappers.lambdaQuery(ClassInfoDO.class)
                .eq(ClassInfoDO::getClassNum, requestParam.getClassNum())
                .eq(ClassInfoDO::getDelFlag, 0);
        ClassInfoDO originalClassInfo = classInfoMapper.selectOne(classInfoWrapper);

        if (originalClassInfo == null) {
            throw new ClientException("您修改的班级不存在");
        }

        // 查询原始专业和学院信息
        Integer originalMajorNum = originalClassInfo.getMajorNum();
        LambdaQueryWrapper<MajorAndAcademyDO> MAWrapper = Wrappers.lambdaQuery(MajorAndAcademyDO.class)
                .eq(MajorAndAcademyDO::getMajorNum, originalMajorNum);
        MajorAndAcademyDO originalMAInfo = majorAndAcademyMapper.selectOne(MAWrapper);

        if (originalMAInfo == null) {
            throw new ClientException("关联的专业信息不存在");
        }

        // 检查哪些字段需要更新
        boolean classNameChanged = StringUtils.isNotBlank(requestParam.getClassName())
                && !requestParam.getClassName().equals(originalClassInfo.getClassName());
        boolean majorNumChanged = requestParam.getMajorNum() != null
                && !requestParam.getMajorNum().equals(originalMajorNum);

        // 更新班级信息（如果有变更）
        if (classNameChanged || majorNumChanged) {
            ClassInfoDO updateClassInfoDO = new ClassInfoDO();
            updateClassInfoDO.setId(originalClassInfo.getId());

            if (classNameChanged) {
                updateClassInfoDO.setClassName(requestParam.getClassName());
            }
            if (majorNumChanged) {
                updateClassInfoDO.setMajorNum(requestParam.getMajorNum());
            }

            classInfoMapper.updateById(updateClassInfoDO);
        }

        // 检查专业和学院信息是否需要更新
        boolean majorNameChanged = StringUtils.isNotBlank(requestParam.getMajorName())
                && !requestParam.getMajorName().equals(originalMAInfo.getMajor());
        boolean academyNumChanged = requestParam.getAcademyNum() != null
                && !requestParam.getAcademyNum().equals(originalMAInfo.getAcademyNum());
        boolean academyNameChanged = StringUtils.isNotBlank(requestParam.getAcademyName())
                && !requestParam.getAcademyName().equals(originalMAInfo.getAcademy());

        // 更新专业和学院信息（如果有变更）
        if (majorNumChanged || majorNameChanged || academyNumChanged || academyNameChanged) {
            MajorAndAcademyDO updateMA = new MajorAndAcademyDO();
            // updateMA.setId(originalMAInfo.getId());

            if (majorNumChanged) {
                updateMA.setMajorNum(requestParam.getMajorNum());
            }
            if (majorNameChanged) {
                updateMA.setMajor(requestParam.getMajorName());
            }
            if (academyNumChanged) {
                updateMA.setAcademyNum(requestParam.getAcademyNum());
            }
            if (academyNameChanged) {
                updateMA.setAcademy(requestParam.getAcademyName());
            }

            majorAndAcademyMapper.updateById(updateMA);
        }

        // 如果专业编号或班级名称有变更，需要更新学生表，并且清理缓存
        if (majorNumChanged || classNameChanged) {
            updateStudentInfo(String.valueOf(requestParam.getClassNum()),
                    majorNumChanged ? requestParam.getMajorNum() : originalMajorNum
            );
            baseInfoCacheService.clearStudentContactCacheByClass(requestParam.getClassNum());
            baseInfoCacheService.evictPageCacheByClass(requestParam.getClassNum());
        }
    }

    /**
     * 更新专业信息
     *
     * @param requestParam 请求参数
     */
    @Override
    public void updateBaseMajorInfo(BaseMajorInfoUpdateReqDTO requestParam) {
        if(requestParam==null){
            throw new ClientException("请求参数不能为空");
        }
        LambdaQueryWrapper<MajorAndAcademyDO> originalWrapper = Wrappers.lambdaQuery(MajorAndAcademyDO.class)
                .eq(MajorAndAcademyDO::getMajorNum, requestParam.getMajorNum())
                .eq(MajorAndAcademyDO::getDelFlag, 0);
        MajorAndAcademyDO originalMADO = majorAndAcademyMapper.selectOne(originalWrapper);
        if (originalMADO == null) {
            throw new ClientException("您查询的专业不存在");
        }
        boolean majorNameChanged = StringUtils.isNotBlank(requestParam.getMajorName())
                && !requestParam.getMajorName().equals(originalMADO.getMajor());
        boolean academyNameChanged = StringUtils.isNotBlank(requestParam.getAcademyName())
                && !requestParam.getAcademyName().equals(originalMADO.getAcademy());
        if(majorNameChanged || academyNameChanged){
            MajorAndAcademyDO updateMADO = new MajorAndAcademyDO();
            if(majorNameChanged){
                updateMADO.setMajor(requestParam.getMajorName());
            }
            if(academyNameChanged){
                updateMADO.setAcademy(requestParam.getAcademyName());
            }
            majorAndAcademyMapper.update(updateMADO,null);
            baseInfoCacheService.clearStudentContactCacheByMajor(requestParam.getMajorNum());
        }



    }

    /**
     * 更新学院基础信息
     *
     * @param requestParam 请求参数
     */
    @Override
    public void updateBaseAcademyInfo(BaseAcademyInfoUpdateReqDTO requestParam) {
        if(requestParam==null){
            throw new ClientException("请求参数不能为空");
        }
        LambdaQueryWrapper<MajorAndAcademyDO> originalWrapper = Wrappers.lambdaQuery(MajorAndAcademyDO.class)
                .eq(MajorAndAcademyDO::getAcademyNum, requestParam.getAcademyNum())
                .eq(MajorAndAcademyDO::getDelFlag, 0);
        MajorAndAcademyDO originalMADO = majorAndAcademyMapper.selectOne(originalWrapper);
        if (originalMADO == null) {
            throw new ClientException("您查询的学院不存在");
        }
        boolean academyNameChanged = StringUtils.isNotBlank(requestParam.getAcademyName())
                && !requestParam.getAcademyName().equals(originalMADO.getAcademy());
        if(academyNameChanged){
            MajorAndAcademyDO updateMADO = new MajorAndAcademyDO();
            updateMADO.setAcademy(requestParam.getAcademyName());
            majorAndAcademyMapper.update(updateMADO,null);
            baseInfoCacheService.clearStudentContactCacheByAcademy(requestParam.getAcademyNum());
        }
    }

    /**
     * 更新学生信息表中的专业和班级信息
     *
     * @param classNum 班级编号
     * @param majorNum 专业编号
     */
    private void updateStudentInfo(String classNum, Integer majorNum) {
        // 更新StudentDO表
        StudentFrameworkDO studentUpdate = new StudentFrameworkDO();
        studentUpdate.setMajorNum(String.valueOf(majorNum));
        studentUpdate.setClassNum(classNum);

        LambdaUpdateWrapper<StudentFrameworkDO> studentWrapper = Wrappers.lambdaUpdate(StudentFrameworkDO.class)
                .eq(StudentFrameworkDO::getClassNum, classNum);
        studentFrameWorkMapper.update(studentUpdate, studentWrapper);

        // 更新StudentDefaultInfoDO表
        StudentDefaultInfoDO studentDefaultUpdate = new StudentDefaultInfoDO();
        studentDefaultUpdate.setMajorNum(String.valueOf(majorNum));
        studentDefaultUpdate.setClassNum(classNum);

        LambdaUpdateWrapper<StudentDefaultInfoDO> studentDefaultWrapper = Wrappers.lambdaUpdate(StudentDefaultInfoDO.class)
                .eq(StudentDefaultInfoDO::getClassNum, classNum);
        studentDefaultInfoMapper.update(studentDefaultUpdate, studentDefaultWrapper);
        baseInfoCacheService.clearStudentContactCacheByClass(Integer.valueOf(classNum));
        baseInfoCacheService.evictPageCacheByClass(Integer.valueOf(classNum));
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
        Page<StudentFrameworkDO> page = new Page<>(current, size);

        LambdaQueryWrapper<StudentFrameworkDO> queryWrapper = Wrappers.lambdaQuery(StudentFrameworkDO.class)
                .eq(StudentFrameworkDO::getClassNum, requestParam.getClassNum())
                .eq(StudentFrameworkDO::getDelFlag, 0)
                .orderByAsc(StudentFrameworkDO::getStudentId);

        IPage<StudentFrameworkDO> studentPage = studentFrameWorkMapper.selectPage(page, queryWrapper);

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
    private BaseClassInfoListStuRespDTO buildFullResponse(StudentFrameworkDO student, ContactDO contact) {
        // 获取注册时间
        RegisterDO registerInfo = registerMapper.selectOne(Wrappers.lambdaQuery(RegisterDO.class)
                .eq(RegisterDO::getStudentId, student.getStudentId())
                .eq(RegisterDO::getDelFlag, 0));
        Date registrationTime = registerInfo != null ? registerInfo.getCreateTime() : null;

        // 获取最新登录时间
        LoginLogDO lastLogin = loginLogMapper.selectLastLoginByStudentId(student.getStudentId());
        Date lastLoginTime = lastLogin != null ? lastLogin.getUpdateTime() : null;
        return BaseClassInfoListStuRespDTO.builder()
                .studentId(student.getStudentId())
                .name(student.getName())
                .enrollmentYear(student.getEnrollmentYear())
                .graduationYear(student.getGraduationYear())
                .phone(student.getPhone())
                .email(student.getEmail())
                .employer(contact != null ? contact.getEmployer() : null)
                .city(contact != null ? contact.getCity() : null)
                .registrationTime(registrationTime)
                .lastLoginTime(lastLoginTime)
                .build();
    }
}
