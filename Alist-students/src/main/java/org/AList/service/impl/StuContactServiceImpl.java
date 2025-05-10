package org.AList.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.AList.common.biz.user.StuIdContext;
import org.AList.common.convention.exception.ClientException;
import org.AList.domain.dao.entity.ContactDO;
import org.AList.domain.dao.entity.ContactGotoDO;
import org.AList.domain.dao.mapper.ContactGotoMapper;
import org.AList.domain.dao.mapper.ContactMapper;
import org.AList.domain.dto.req.*;
import org.AList.domain.dto.resp.ContactQueryRespDTO;
import org.AList.service.StuContactService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 通讯信息服务实现层
 */
@Service
@RequiredArgsConstructor
public class StuContactServiceImpl extends ServiceImpl<ContactMapper, ContactDO> implements StuContactService {
    private final ContactMapper contactMapper;
    private final ContactGotoMapper contactGotoMapper;
    /**
     * 新增个人通讯信息
     *
     * @param requestParam 新增通讯信息请求体
     */
    @Override
    public void addStudentContact(ContactAddReqDTO requestParam) {
        StuIdContext.verifyLoginUser(requestParam.getStudentId());
        ContactDO contact =ContactDO.builder()
                .studentId(requestParam.getStudentId())
                .employer(requestParam.getEmployer())
                .city(requestParam.getCity())
                .build();
        contactMapper.insert(contact);
    }

    /**
     * 删除个人通讯信息
     *
     * @param requestParam 删除通讯信息请求体
     */
    @Override
    public void deleteStudentContact(ContactDeleteReqDTO requestParam) {
        // 1. 验证当前登录用户
        StuIdContext.verifyLoginUser(requestParam.getOwnerId());

        // 2. 首先检查goto表中是否存在该ownerId和contactId的记录，验证用户是否拥有该通讯录
        LambdaQueryWrapper<ContactGotoDO> gotoQueryWrapper = Wrappers.lambdaQuery(ContactGotoDO.class)
                .eq(ContactGotoDO::getOwnerId, requestParam.getOwnerId())
                .eq(ContactGotoDO::getContactId, requestParam.getContactId())
                .eq(ContactGotoDO::getDelFlag, 0);
        ContactGotoDO contactGoto = contactGotoMapper.selectOne(gotoQueryWrapper);

        if (Objects.isNull(contactGoto)) {
            throw new ClientException("您没有权限删除此通讯录信息或记录不存在");
        }

        // 3. 逻辑删除Contact表中的记录（设置del_flag=1）
        LambdaUpdateWrapper<ContactDO> updateWrapper = Wrappers.lambdaUpdate(ContactDO.class)
                .eq(ContactDO::getStudentId, requestParam.getContactId())
                .eq(ContactDO::getDelFlag, 0)
                .set(ContactDO::getDelFlag, 1);
        int updated = contactMapper.update(null, updateWrapper);

        if (updated == 0) {
            throw new ClientException("删除个人通讯信息出现异常，请重试");
        }

        // 4. 同时逻辑删除goto表中的关联记录（根据业务需求决定）
        LambdaUpdateWrapper<ContactGotoDO> gotoUpdateWrapper = Wrappers.lambdaUpdate(ContactGotoDO.class)
                .eq(ContactGotoDO::getOwnerId, requestParam.getOwnerId())
                .eq(ContactGotoDO::getContactId, requestParam.getContactId())
                .eq(ContactGotoDO::getDelFlag, 0)
                .set(ContactGotoDO::getDelFlag, 1);
        contactGotoMapper.update(null, gotoUpdateWrapper);
    }

    /**
     * 修改个人通讯信息
     *
     * @param requestParam 修改通讯信息请求体
     */
    @Override
    public void updateStudentContact(ContactUpdateReqDTO requestParam) {
        StuIdContext.verifyLoginUser(requestParam.getStudentId());
        LambdaUpdateWrapper<ContactDO> updateWrapper = Wrappers.lambdaUpdate(ContactDO.class)
                .eq(ContactDO::getStudentId, requestParam.getStudentId())
                .eq(ContactDO::getDelFlag, 0);
        if(Objects.isNull(contactMapper.selectOne(updateWrapper))){
            throw new ClientException("修改的记录不存在");
        }
        ContactDO contact = new ContactDO();
        int update = contactMapper.update(contact, updateWrapper);
        if(update != 1){
            throw new ClientException("修改错误");
        }
    }

    /**
     * 按学号查询通讯录信息
     *
     * @param requestParam 查询通讯信息请求体
     * @return 单个学生的通讯信息
     */
    @Override
    public ContactQueryRespDTO queryContactById(ContactQueryByIdReqDTO requestParam) {
        // 1. 验证当前登录用户
        StuIdContext.verifyLoginUser(requestParam.getOwnerId());

        // 2. 首先检查goto表中是否存在该ownerId和contactId的记录，验证用户是否拥有该通讯录
        LambdaQueryWrapper<ContactGotoDO> gotoQueryWrapper = Wrappers.lambdaQuery(ContactGotoDO.class)
                .eq(ContactGotoDO::getOwnerId, requestParam.getOwnerId())
                .eq(ContactGotoDO::getContactId, requestParam.getContactId())
                .eq(ContactGotoDO::getDelFlag, 0);
        ContactGotoDO contactGoto = contactGotoMapper.selectOne(gotoQueryWrapper);

        if (Objects.isNull(contactGoto)) {
            throw new ClientException("您没有权限查看此通讯录信息或记录不存在");
        }

        // 3. 从contact表中查询完整的通讯录信息
        LambdaQueryWrapper<ContactDO> contactQueryWrapper = Wrappers.lambdaQuery(ContactDO.class)
                .eq(ContactDO::getStudentId, requestParam.getContactId())
                .eq(ContactDO::getDelFlag, 0);
        ContactDO contact = contactMapper.selectOne(contactQueryWrapper);

        if (Objects.isNull(contact)) {
            throw new ClientException("通讯录信息不存在或已被删除");
        }

        // 4. 转换为响应DTO
        ContactQueryRespDTO respDTO = new ContactQueryRespDTO();
        BeanUtils.copyProperties(contact, respDTO);

        return respDTO;
    }

    /**
     * 分页查询个人全量通讯信息
     *
     * @return 分页返回
     */
    @Override
    public IPage<ContactQueryRespDTO> queryContactList(ContactQueryAllOwnReqDTO requestParam) {
        String ownerId=requestParam.getOwnerId();
        LambdaQueryWrapper<ContactGotoDO> queryWrapper = Wrappers.lambdaQuery(ContactGotoDO.class)
                .eq(ContactGotoDO::getOwnerId, ownerId)
                .eq(ContactGotoDO::getDelFlag, 0);
        List<ContactGotoDO> gotoList = contactGotoMapper.selectList(queryWrapper);
        if(Objects.isNull(gotoList)){
            return new Page<>(1,10,0);
        }
        List<String> contactIds=gotoList.stream()
                .map(ContactGotoDO::getContactId)
                .toList();
        LambdaQueryWrapper<ContactDO> contactQueryWrapper = Wrappers.lambdaQuery(ContactDO.class)
                .in(ContactDO::getStudentId, contactIds)
                .eq(ContactDO::getDelFlag, 0);
        Page<ContactDO> page = new Page<>(1,10);
        IPage<ContactDO> contactPage = contactMapper.selectPage(page, contactQueryWrapper);
        return contactPage.convert(contactDO -> {
            ContactQueryRespDTO respDTO = new ContactQueryRespDTO();
            // 这里进行属性拷贝，可以使用BeanUtils或者手动set
            BeanUtils.copyProperties(contactDO, respDTO);
            return respDTO;
        });
    }

    /**
     * 恢复删除的通讯信息
     *
     * @param requestParam 恢复通讯信息请求体
     */
    @Override
    public void restoreStudentContact(ContactRestoreReqDTO requestParam) {
        StuIdContext.verifyLoginUser(requestParam.getOwnerId());
        ContactGotoDO deletedContactGotoDO=contactGotoMapper.selectSingleDeletedContactGoto(
                requestParam.getContactId(),
                requestParam.getOwnerId(),
                1);
        if(Objects.isNull(deletedContactGotoDO)){
            throw new ClientException("您没有权限恢复此通讯录信息或记录不存在");
        }
        ContactDO deletedContact=contactMapper.selectSingleDeletedContact(
                requestParam.getContactId(),
                1
        );
        if(Objects.isNull(deletedContact)){
            throw new ClientException("您没有权限恢复此通讯录信息或记录不存在");
        }
        int restoreGoto= contactGotoMapper.restoreContactGoto(requestParam.getContactId(), requestParam.getOwnerId());
        int restoreContact= contactMapper.restoreContact(requestParam.getContactId());
        if(restoreGoto!=1||restoreContact!=1){
            throw new ClientException("恢复失败，记录可能不存在或未被删除");
        }
    }

    private void rebuildContactCache(String studentId) {
        // 1. 先清除所有相关缓存
        String patternKey = String.format("contact:*:%s", studentId);
        Set<String> keys = stringRedisTemplate.keys(patternKey);
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }

        // 2. 查询联系人完整信息（只需查一次数据库）
        ContactDO contact = contactMapper.selectOne(Wrappers.lambdaQuery(ContactDO.class)
                .eq(ContactDO::getStudentId, studentId)
                .eq(ContactDO::getDelFlag, 0));

        if (contact == null) {
            return; // 如果联系人已被删除，直接返回
        }

        // 3. 查询学生基本信息
        StudentDO student = studentMapper.selectOne(Wrappers.lambdaQuery(StudentDO.class)
                .eq(StudentDO::getStudentId, studentId)
                .eq(StudentDO::getDelFlag, 0));

        if (student == null) {
            return;
        }

        // 4. 查询关联信息（专业、学院等）
        MajorAndAcademyDO majorAndAcademy = majorAndAcademyMapper.selectOne(
                Wrappers.lambdaQuery(MajorAndAcademyDO.class)
                        .eq(MajorAndAcademyDO::getMajorNum, student.getMajor())
                        .eq(MajorAndAcademyDO::getDelFlag, 0));

        ClassInfoDO classInfo = classInfoMapper.selectOne(
                Wrappers.lambdaQuery(ClassInfoDO.class)
                        .eq(ClassInfoDO::getClassNum, student.getClassName())
                        .eq(ClassInfoDO::getDelFlag, 0));

        // 5. 构建完整响应DTO
        ContactQueryRespDTO response = ContactQueryRespDTO.builder()
                .studentId(studentId)
                .name(student.getName())
                .academy(majorAndAcademy != null ? majorAndAcademy.getAcademy() : null)
                .major(majorAndAcademy != null ? majorAndAcademy.getMajor() : null)
                .className(classInfo != null ? classInfo.getClassName() : null)
                .enrollmentYear(student.getEnrollmentYear())
                .graduationYear(student.getGraduationYear())
                .employer(contact.getEmployer())
                .city(contact.getCity())
                .phone(student.getPhone())
                .email(student.getEmail())
                .build();

        // 6. 获取所有拥有者ID
        List<String> ownerIds = contactGotoMapper.selectOwnerIdsByContactId(studentId);

        // 7. 直接为每个拥有者设置缓存
        for (String ownerId : ownerIds) {
            String redisKey = String.format("contact:%s:%s", ownerId, studentId);
            try {
                // 使用Jackson将DTO转换为JSON字符串
                String jsonResponse = objectMapper.writeValueAsString(response);
                stringRedisTemplate.opsForValue().set(
                        redisKey,
                        jsonResponse,
                        1,
                        TimeUnit.HOURS
                );
            } catch (JsonProcessingException e) {
                log.error("序列化联系人缓存失败，studentId: {}", studentId, e);
                // 可以选择继续处理下一个ownerId或抛出运行时异常
            }
        }
    }
}
