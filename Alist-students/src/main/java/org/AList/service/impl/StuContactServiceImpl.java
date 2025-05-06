package org.AList.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.AList.common.biz.user.StuIdContext;
import org.AList.common.convention.exception.ClientException;
import org.AList.domain.dao.entity.ContactDO;
import org.AList.domain.dao.mapper.ContactMapper;
import org.AList.domain.dto.req.AddContactReqDTO;
import org.AList.domain.dto.req.DeleteContactReqDTO;
import org.AList.service.StuContactService;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 通讯信息服务实现层
 */
@Service
@RequiredArgsConstructor
public class StuContactServiceImpl extends ServiceImpl<ContactMapper, ContactDO> implements StuContactService {
    private final ContactMapper contactMapper;
    /**
     * 新增通讯信息
     *
     * @param requestParam 新增通讯信息请求体
     */
    @Override
    public void addStudentContact(AddContactReqDTO requestParam) {
        StuIdContext.verifyLoginUser(requestParam.getStudentId());
        ContactDO contact =ContactDO.builder()
                .studentId(requestParam.getStudentId())
                .employer(requestParam.getEmployer())
                .city(requestParam.getCity())
                .build();
        contactMapper.insert(contact);
    }

    /**
     * 删除通讯信息
     *
     * @param requestParam 删除通讯信息请求体
     */
    @Override
    public void deleteStudentContact(DeleteContactReqDTO requestParam) {
        StuIdContext.verifyLoginUser(requestParam.getStudentId());
        LambdaQueryWrapper<ContactDO> queryWrapper = Wrappers.lambdaQuery(ContactDO.class)
                .eq(ContactDO::getStudentId, requestParam.getStudentId())
                .eq(ContactDO::getDelFlag, 0);
        ContactDO contactDO = contactMapper.selectOne(queryWrapper);
        if(Objects.isNull(contactDO)){
            throw new ClientException("删除的记录不存在");
        }
        ContactDO delContact = new ContactDO();
        delContact.setDelFlag(1);
        contactMapper.update(delContact,queryWrapper);

    }
}
