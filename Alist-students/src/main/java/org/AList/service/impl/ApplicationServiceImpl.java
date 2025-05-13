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
import org.AList.service.bloom.StudentIdBloomFilterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.AList.common.enums.UserErrorCodeEnum.USER_NULL;

/**
 * 站内信方法实现类
 */
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, ApplicationDO> implements ApplicationService {
    private final StudentFrameWorkMapper studentFrameWorkMapper;
    private final ApplicationMapper applicationMapper;
    private final ContactGotoMapper contactGotoMapper;
    private final StudentIdBloomFilterService studentIdBloomFilterService;

    /**
     * 向某人发送
     *
     * @param requestParam 发送请求实体
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void sendApplication(ApplicationSendMsgReqDTO requestParam) {
        // 最先查一下这个收信人，是不是存在于布隆过滤器当中 假如你发送的学生都不是这个学校的，直接提供快速返回的机制
        if(!studentIdBloomFilterService.contain(requestParam.getReceiver())){
            throw new ClientException(USER_NULL);
        }
        // 自己不能给自己发
        if(Objects.equals(requestParam.getReceiver(), StuIdContext.getStudentId())){
            throw new ClientException("您不能给自己发送申请");
        }


        // 先判断接收者是不是存在 也就是接收者是不是注册过
        LambdaQueryWrapper<StudentFrameworkDO> validQueryWrapper = Wrappers.lambdaQuery(StudentFrameworkDO.class)
                .eq(StudentFrameworkDO::getStudentId, requestParam.getReceiver())
                .eq(StudentFrameworkDO::getStatus, 1)
                .eq(StudentFrameworkDO::getDelFlag, 0);
        StudentFrameworkDO receiverDO = studentFrameWorkMapper.selectOne(validQueryWrapper);

        if(receiverDO == null) {
            throw new ClientException("收信人状态异常，无法添加到通讯录");
        }

        // 已经给这个人发过不能再继续发申请 在对面没拒绝的情况下 匹配上发送者和接收者的数据条数超过一条 那就说明你重复发送了
        LambdaQueryWrapper<ApplicationDO> uniqueMsgQueryWrapper = Wrappers.lambdaQuery(ApplicationDO.class)
                .eq(ApplicationDO::getSender, StuIdContext.getStudentId())
                .eq(ApplicationDO::getReceiver, requestParam.getReceiver())
                .ne(ApplicationDO::getStatus, 2)
                .eq(ApplicationDO::getDelFlag, 0);
        Long selectCount = applicationMapper.selectCount(uniqueMsgQueryWrapper);
        if(selectCount > 1){
            throw new ClientException("您已经给对方发送过申请，请等待对方处理请求");
        }
        LambdaQueryWrapper<StudentFrameworkDO> senderWrapper = Wrappers.lambdaQuery(StudentFrameworkDO.class)
                .eq(StudentFrameworkDO::getStudentId, StuIdContext.getStudentId());
        StudentFrameworkDO sender = studentFrameWorkMapper.selectOne(senderWrapper);
        String senderName=sender.getName();
        // 假如说这个接收者存在 直接写库是不是就行了
        ApplicationDO applicationDO =ApplicationDO.builder()
                .sender(StuIdContext.getStudentId())
                .senderName(senderName)
                .receiver(requestParam.getReceiver())
                .receiverName(receiverDO.getName())
                .content(requestParam.getContent())
                .status(0)
                .build();
        int insert = applicationMapper.insert(applicationDO);
        if(insert != 1) {
            throw new ClientException("发送请求失败，请重新操作");
        }
    }

    /**
     * 展示接受的 所有没删除 没审核的站内信请求
     *
     * @return 分页结果
     */
    @Override
    public IPage<ApplicationQueryPageRespDTO> listAllValidApplication(ApplicationReceiveQueryPageReqDTO requestParam) {
        // 为了让前端好过一点 尽可能地让所有方法都显式地传入参数
        LambdaQueryWrapper<ApplicationDO> queryWrapper = Wrappers.lambdaQuery(ApplicationDO.class)
                .eq(ApplicationDO::getReceiver, requestParam.getReceiver())
                .eq(ApplicationDO::getStatus, 0)
                .eq(ApplicationDO::getDelFlag, 0);
        IPage<ApplicationDO> resultPage=baseMapper.selectPage(requestParam,queryWrapper);
        return resultPage.convert(each-> BeanUtil.toBean(each, ApplicationQueryPageRespDTO.class));
    }

    /**
     * 展示接受的 没删除 已通过的站内信请求
     *
     * @return 分页结果
     */
    @Override
    public IPage<ApplicationQueryPageRespDTO> listAllAcceptedApplication(ApplicationReceiveQueryPageReqDTO requestParam) {
        LambdaQueryWrapper<ApplicationDO> queryWrapper = Wrappers.lambdaQuery(ApplicationDO.class)
                .eq(ApplicationDO::getReceiver, requestParam.getReceiver())
                .eq(ApplicationDO::getStatus, 1)
                .eq(ApplicationDO::getDelFlag, 0);
        IPage<ApplicationDO> resultPage=baseMapper.selectPage(requestParam,queryWrapper);
        return resultPage.convert(each-> BeanUtil.toBean(each, ApplicationQueryPageRespDTO.class));
    }

    /**
     * 展示接受的 没删除 已拒绝的站内信请求
     *
     * @param requestParam 传入参数-当前登录的学生学号-接收用户
     * @return 分页结果
     */
    @Override
    public IPage<ApplicationQueryPageRespDTO> listAllRefusedApplication(ApplicationReceiveQueryPageReqDTO requestParam) {
        LambdaQueryWrapper<ApplicationDO> queryWrapper = Wrappers.lambdaQuery(ApplicationDO.class)
                .eq(ApplicationDO::getReceiver, requestParam.getReceiver())
                .eq(ApplicationDO::getStatus, 2)
                .eq(ApplicationDO::getDelFlag, 0);
        IPage<ApplicationDO> resultPage=baseMapper.selectPage(requestParam,queryWrapper);
        return resultPage.convert(each-> BeanUtil.toBean(each, ApplicationQueryPageRespDTO.class));
    }

    /**
     * 展示已删除的站内信申请
     *
     * @param requestParam 传入参数-当前登录的学生学号-接收用户
     * @return 分页结果
     */
    @Override

    public IPage<ApplicationQueryPageRespDTO> listAllDeleteApplication(ApplicationReceiveQueryPageReqDTO requestParam) {
        // 1. 构造分页参数
        Page<ApplicationDO> page = new Page<>(1,10);

        // 2. 调用自定义SQL查询
        IPage<ApplicationDO> resultPage = applicationMapper.selectDeletedApplications(
                page,
                requestParam.getReceiver(),
                1  // delFlag = 1（查询已删除数据）
        );

        // 3. 转换为 DTO
        return resultPage.convert(each -> BeanUtil.toBean(each, ApplicationQueryPageRespDTO.class));
    }

    /**
     * 展示已发送的站内信请求
     *
     * @param requestParam 传入参数-消息的sender
     * @return void
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
     * 同意某个站内信申请
     *
     * @param requestParam 同意或者拒绝操作请求体
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
            applicationDO.setStatus(1); // 修改状态为1
            // 调用 MyBatis Plus 的 updateById 方法进行更新
            baseMapper.updateById(applicationDO);
            // 既然同意了站内信申请，就需要把自己的联系信息展示给sender
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
            // 处理未找到记录的情况，可抛异常或返回提示信息
            throw new ClientException("未找到待处理的申请记录");
        }
    }

    /**
     * 拒绝某个站内信申请
     *
     * @param requestParam 同意或者拒绝操作请求体
     */
    @Override
    public void refuseSingleApplication(ApplicationYONReqDTO requestParam) {
        LambdaQueryWrapper<ApplicationDO> queryWrapper = Wrappers.lambdaQuery(ApplicationDO.class)
                .eq(ApplicationDO::getReceiver, requestParam.getReceiver())
                .eq(ApplicationDO::getSender, requestParam.getSender())
                .eq(ApplicationDO::getStatus, 0)
                .eq(ApplicationDO::getDelFlag, 0);
        ApplicationDO applicationDO = baseMapper.selectOne(queryWrapper);
        if (applicationDO != null) {
            applicationDO.setStatus(2); // 修改状态为2
            // 调用 MyBatis Plus 的 updateById 方法进行更新
            baseMapper.updateById(applicationDO);
        } else {
            // 处理未找到记录的情况，可抛异常或返回提示信息
            throw new RuntimeException("未找到待处理的申请记录");
        }
    }

    /**
     * 删除某个站内信申请
     *
     * @param requestParam 同意或者拒绝操作请求体
     */
    @Override
    @Transactional
    public void deleteSingleApplication(ApplicationYONReqDTO requestParam) {
        LambdaUpdateWrapper<ApplicationDO> updateWrapper = Wrappers.lambdaUpdate(ApplicationDO.class)
                .eq(ApplicationDO::getReceiver, requestParam.getReceiver())
                .eq(ApplicationDO::getSender, requestParam.getSender())
                .eq(ApplicationDO::getDelFlag, 0)
                .set(ApplicationDO::getDelFlag, 1);  // 直接在这里设置 del_flag=1
        int updated = applicationMapper.update(null, updateWrapper);
        if (updated == 0) {
            throw new RuntimeException("未找到待处理的申请记录");
        }
    }

}
