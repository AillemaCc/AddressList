package org.AList.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.AList.common.biz.user.StuIdContext;
import org.AList.domain.dao.entity.ContactDO;
import org.AList.domain.dao.mapper.ContactMapper;
import org.AList.domain.dto.req.AddContactReqDTO;
import org.AList.service.StuContactService;
import org.springframework.stereotype.Service;

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
}
