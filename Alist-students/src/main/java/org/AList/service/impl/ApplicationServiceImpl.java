package org.AList.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.AList.common.biz.user.StuIdContext;
import org.AList.common.convention.exception.ClientException;
import org.AList.common.enums.StudentChainMarkEnum;
import org.AList.designpattern.chain.AbstractChainContext;
import org.AList.domain.dao.entity.ApplicationDO;
import org.AList.domain.dao.entity.ContactGotoDO;
import org.AList.domain.dao.entity.StudentFrameworkDO;
import org.AList.domain.dao.mapper.ApplicationMapper;
import org.AList.domain.dao.mapper.ContactGotoMapper;
import org.AList.domain.dao.mapper.StudentFrameWorkMapper;
import org.AList.domain.dto.req.ApplicationReceiveQueryPageReqDTO;
import org.AList.domain.dto.req.ApplicationSendMsgReqDTO;
import org.AList.domain.dto.req.ApplicationSendQueryPageReqDTO;
import org.AList.domain.dto.req.ApplicationYONReqDTO;
import org.AList.domain.dto.resp.ApplicationQueryPageRespDTO;
import org.AList.service.ApplicationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 站内信方法实现类
 */
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, ApplicationDO> implements ApplicationService {
    private final StudentFrameWorkMapper studentFrameWorkMapper;
    private final ApplicationMapper applicationMapper;
    private final ContactGotoMapper contactGotoMapper;
    private final AbstractChainContext<ApplicationSendMsgReqDTO> abstractChainContext;


    /**
     * 发送站内信申请
     *
     * @param requestParam 发送请求参数，包含接收者ID和消息内容
     * @throws ClientException 如果接收者不存在、发送给自己、或已发送过申请
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void sendApplication(ApplicationSendMsgReqDTO requestParam) {
        abstractChainContext.handler(StudentChainMarkEnum.APPLICATION_SEND_FILTER.name(), requestParam);
        LambdaQueryWrapper<StudentFrameworkDO> senderWrapper = Wrappers.lambdaQuery(StudentFrameworkDO.class)
                .eq(StudentFrameworkDO::getStudentId, StuIdContext.getStudentId());
        StudentFrameworkDO sender = studentFrameWorkMapper.selectOne(senderWrapper);
        String senderName=sender.getName();

        LambdaQueryWrapper<StudentFrameworkDO> receiverWrapper = Wrappers.lambdaQuery(StudentFrameworkDO.class)
                .eq(StudentFrameworkDO::getStudentId, requestParam.getReceiver());
        StudentFrameworkDO receiver = studentFrameWorkMapper.selectOne(receiverWrapper);
        String receiverName=receiver.getName();
        ApplicationDO applicationDO =ApplicationDO.builder()
                .sender(StuIdContext.getStudentId())
                .senderName(senderName)
                .receiver(requestParam.getReceiver())
                .receiverName(receiverName)
                .content(requestParam.getContent())
                .status(0)
                .build();
        int insert = applicationMapper.insert(applicationDO);
        if(insert != 1) {
            throw new ClientException("发送请求失败，请重新操作");
        }
    }

    /**
     * 查询所有有效的（未处理）站内信申请
     *
     * @param requestParam 分页查询参数，包含接收者ID
     * @return 分页查询结果，包含站内信列表
     */
    @Override
    public IPage<ApplicationQueryPageRespDTO> listAllValidApplication(ApplicationReceiveQueryPageReqDTO requestParam) {
        int current=requestParam.getCurrent()==null?0:requestParam.getCurrent();
        int size=requestParam.getSize()==null?10:requestParam.getSize();
        LambdaQueryWrapper<ApplicationDO> queryWrapper = buildBaseQueryWrapper(requestParam.getReceiver(),0);
        IPage<ApplicationDO> resultPage=page(new Page<>(current,size),queryWrapper);
        return resultPage.convert(each-> BeanUtil.toBean(each, ApplicationQueryPageRespDTO.class));
    }

    /**
     * 查询所有已同意的站内信申请
     *
     * @param requestParam 分页查询参数，包含接收者ID
     * @return 分页查询结果，包含站内信列表
     */
    @Override
    public IPage<ApplicationQueryPageRespDTO> listAllAcceptedApplication(ApplicationReceiveQueryPageReqDTO requestParam) {
        int current=requestParam.getCurrent()==null?0:requestParam.getCurrent();
        int size=requestParam.getSize()==null?10:requestParam.getSize();
        LambdaQueryWrapper<ApplicationDO> queryWrapper = buildBaseQueryWrapper(requestParam.getReceiver(),1);
        IPage<ApplicationDO> resultPage=page(new Page<>(current,size),queryWrapper);
        return resultPage.convert(each-> BeanUtil.toBean(each, ApplicationQueryPageRespDTO.class));
    }

    /**
     * 查询所有已拒绝的站内信申请
     *
     * @param requestParam 分页查询参数，包含接收者ID
     * @return 分页查询结果，包含站内信列表
     */
    @Override
    public IPage<ApplicationQueryPageRespDTO> listAllRefusedApplication(ApplicationReceiveQueryPageReqDTO requestParam) {
        int current=requestParam.getCurrent()==null?0:requestParam.getCurrent();
        int size=requestParam.getSize()==null?10:requestParam.getSize();
        LambdaQueryWrapper<ApplicationDO> queryWrapper = buildBaseQueryWrapper(requestParam.getReceiver(),2);
        IPage<ApplicationDO> resultPage=page(new Page<>(current,size),queryWrapper);
        return resultPage.convert(each-> BeanUtil.toBean(each, ApplicationQueryPageRespDTO.class));
    }

    /**
     * 查询所有已删除的站内信申请
     *
     * @param requestParam 分页查询参数，包含接收者ID
     * @return 分页查询结果，包含站内信列表
     * &#064;todo  分页查询功能有待测试
     */
    @Override
    public IPage<ApplicationQueryPageRespDTO> listAllDeleteApplication(ApplicationReceiveQueryPageReqDTO requestParam) {
        int current=requestParam.getCurrent()==null?1:requestParam.getCurrent();
        int size=requestParam.getSize()==null?10:requestParam.getSize();
        Page<ApplicationDO> page = new Page<>(current,size);
        IPage<ApplicationDO> resultPage = applicationMapper.selectDeletedApplications(
                page,
                requestParam.getReceiver(),
                1
        );
        return resultPage.convert(each -> BeanUtil.toBean(each, ApplicationQueryPageRespDTO.class));
    }

    /**
     * 查询所有已发送的站内信请求
     *
     * @param requestParam 分页查询参数，包含发送者ID
     * @return 分页查询结果，包含站内信列表
     */
    @Override
    public IPage<ApplicationQueryPageRespDTO> listAllSendApplication(ApplicationSendQueryPageReqDTO requestParam) {
        LambdaQueryWrapper<ApplicationDO> queryWrapper = Wrappers.lambdaQuery(ApplicationDO.class)
                .eq(ApplicationDO::getSender, requestParam.getSender())
                .eq(ApplicationDO::getDelFlag, 0);
        IPage<ApplicationDO> resultPage=baseMapper.selectPage(requestParam,queryWrapper);
        return resultPage.convert(each-> BeanUtil.toBean(each, ApplicationQueryPageRespDTO.class));
    }

    /**
     * 同意单个站内信申请
     * <p>
     * 同意后会将接收者的联系信息展示给发送者
     *
     * @param requestParam 请求参数，包含发送者和接收者ID
     * @throws ClientException 如果未找到申请记录或更新失败
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void acceptSingleApplication(ApplicationYONReqDTO requestParam) {
        LambdaQueryWrapper<ApplicationDO> queryWrapper = Wrappers.lambdaQuery(ApplicationDO.class)
                .eq(ApplicationDO::getReceiver, requestParam.getReceiver())
                .eq(ApplicationDO::getSender, requestParam.getSender())
                .eq(ApplicationDO::getStatus, 0)
                .eq(ApplicationDO::getDelFlag, 0);
        ApplicationDO applicationDO = baseMapper.selectOne(queryWrapper);
        if (applicationDO != null) {
            applicationDO.setStatus(1);
            baseMapper.update(applicationDO,null);
            String contactId=requestParam.getReceiver();
            String ownerId=requestParam.getSender();
            ContactGotoDO contactGotoDO=ContactGotoDO.builder()
                    .contactId(contactId)
                    .ownerId(ownerId)
                    .build();
            int insert = contactGotoMapper.insert(contactGotoDO);
            if(insert != 1) {
                throw new ClientException("同意请求失败，请重试");
            }
        } else {
            throw new ClientException("未找到待处理的申请记录");
        }
    }

    /**
     * 拒绝单个站内信申请
     *
     * @param requestParam 请求参数，包含发送者和接收者ID
     * @throws RuntimeException 如果未找到申请记录
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void refuseSingleApplication(ApplicationYONReqDTO requestParam) {
        LambdaQueryWrapper<ApplicationDO> queryWrapper = Wrappers.lambdaQuery(ApplicationDO.class)
                .eq(ApplicationDO::getReceiver, requestParam.getReceiver())
                .eq(ApplicationDO::getSender, requestParam.getSender())
                .eq(ApplicationDO::getStatus, 0)
                .eq(ApplicationDO::getDelFlag, 0);
        ApplicationDO applicationDO = baseMapper.selectOne(queryWrapper);
        if (applicationDO != null) {
            applicationDO.setStatus(2);
            baseMapper.update(applicationDO,null);
        } else {
            throw new ClientException("未找到待处理的申请记录");
        }
    }

    /**
     * 删除单个站内信申请
     * 逻辑删除，将delFlag设置为1
     *
     * @param requestParam 请求参数，包含发送者和接收者ID
     * @throws RuntimeException 如果未找到申请记录
     */
    @Override
    @Transactional
    public void deleteSingleApplication(ApplicationYONReqDTO requestParam) {
        LambdaUpdateWrapper<ApplicationDO> updateWrapper = Wrappers.lambdaUpdate(ApplicationDO.class)
                .eq(ApplicationDO::getReceiver, requestParam.getReceiver())
                .eq(ApplicationDO::getSender, requestParam.getSender())
                .eq(ApplicationDO::getDelFlag, 0)
                .set(ApplicationDO::getDelFlag, 1);
        int updated = applicationMapper.update(null, updateWrapper);
        if (updated == 0) {
            throw new RuntimeException("未找到待处理的申请记录");
        }
    }


    // 提取公共查询条件
    private LambdaQueryWrapper<ApplicationDO> buildBaseQueryWrapper(String receiver, Integer status) {
        return Wrappers.lambdaQuery(ApplicationDO.class)
                .eq(ApplicationDO::getReceiver, receiver)
                .eq(status != null, ApplicationDO::getStatus, status)
                .eq(ApplicationDO::getDelFlag, 0);
    }


}
