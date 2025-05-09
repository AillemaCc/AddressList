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
        StuIdContext.verifyLoginUser(requestParam.getStudentId());
        LambdaUpdateWrapper<ContactDO> updateWrapper = Wrappers.lambdaUpdate(ContactDO.class)
                .eq(ContactDO::getStudentId, requestParam.getStudentId())
                .eq(ContactDO::getDelFlag, 0)
                .set(ContactDO::getDelFlag, 1);
        int updated=contactMapper.update(null, updateWrapper);
        if(updated==0){
            throw new ClientException("删除个人通讯信息出现异常，请重试");
        }
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
        StuIdContext.verifyLoginUser(requestParam.getStudentId());
        LambdaQueryWrapper<ContactDO> queryWrapper = Wrappers.lambdaQuery(ContactDO.class)
                .eq(ContactDO::getStudentId, requestParam.getStudentId())
                .eq(ContactDO::getDelFlag, 0);
        ContactDO contact = contactMapper.selectOne(queryWrapper);
        if(Objects.isNull(contact)){
            throw new ClientException("查询的记录不存在");
        }
        return BeanUtil.toBean(contact, ContactQueryRespDTO.class);
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
}
