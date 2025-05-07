package org.AList.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.AList.common.biz.user.StuIdContext;
import org.AList.common.convention.exception.ClientException;
import org.AList.domain.dao.entity.ApplicationDO;
import org.AList.domain.dao.entity.StudentDO;
import org.AList.domain.dao.mapper.ApplicationMapper;
import org.AList.domain.dao.mapper.StudentMapper;
import org.AList.domain.dto.req.ApplicationSendMsgReqDTO;
import org.AList.domain.dto.req.QueryApplicationPageReqDTO;
import org.AList.domain.dto.resp.QueryApplicationPageRespDTO;
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
    private final StudentMapper studentMapper;
    private final ApplicationMapper applicationMapper;
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
        if(Objects.equals(requestParam.getReceiver(), StuIdContext.getStudentId())){
            throw new ClientException("您不能给自己发送申请");
        }
        // 先判断发送者是不是存在 也就是发送者是不是注册过
        LambdaQueryWrapper<StudentDO> validQueryWrapper = Wrappers.lambdaQuery(StudentDO.class)
                .eq(StudentDO::getStudentId, requestParam.getReceiver())
                .eq(StudentDO::getStatus, 1)
                .eq(StudentDO::getDelFlag, 0);
        StudentDO receiverDO = studentMapper.selectOne(validQueryWrapper);

        if(receiverDO == null) {
            throw new ClientException("收信人状态异常，无法添加到通讯录");
        }
        LambdaQueryWrapper<StudentDO> senderWrapper = Wrappers.lambdaQuery(StudentDO.class)
                .eq(StudentDO::getStudentId, StuIdContext.getStudentId());
        StudentDO sender = studentMapper.selectOne(senderWrapper);
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
     * 展示所有没删除 没审核的站内信请求
     *
     * @return 分页结果
     */
    @Override
    public IPage<QueryApplicationPageRespDTO> listAllValidApplication(QueryApplicationPageReqDTO requestParam) {
        // 为了让前端好过一点 尽可能地让所有方法都显式地传入参数
        LambdaQueryWrapper<ApplicationDO> queryWrapper = Wrappers.lambdaQuery(ApplicationDO.class)
                .eq(ApplicationDO::getReceiver, requestParam.getReceiver())
                .eq(ApplicationDO::getStatus, 0)
                .eq(ApplicationDO::getDelFlag, 0);
        IPage<ApplicationDO> resultPage=baseMapper.selectPage(requestParam,queryWrapper);
        return resultPage.convert(each-> BeanUtil.toBean(each,QueryApplicationPageRespDTO.class));
    }

    /**
     * 展示没删除 已通过的站内信请求
     *
     * @return 分页结果
     */
    @Override
    public IPage<QueryApplicationPageRespDTO> listAllAcceptedApplication(QueryApplicationPageReqDTO requestParam) {
        LambdaQueryWrapper<ApplicationDO> queryWrapper = Wrappers.lambdaQuery(ApplicationDO.class)
                .eq(ApplicationDO::getReceiver, requestParam.getReceiver())
                .eq(ApplicationDO::getStatus, 1)
                .eq(ApplicationDO::getDelFlag, 0);
        IPage<ApplicationDO> resultPage=baseMapper.selectPage(requestParam,queryWrapper);
        return resultPage.convert(each-> BeanUtil.toBean(each,QueryApplicationPageRespDTO.class));
    }

    /**
     * 展示没删除 已拒绝的站内信请求
     *
     * @param requestParam 传入参数-当前登录的学生学号-接收用户
     * @return 分页结果
     */
    @Override
    public IPage<QueryApplicationPageRespDTO> listAllRefusedApplication(QueryApplicationPageReqDTO requestParam) {
        LambdaQueryWrapper<ApplicationDO> queryWrapper = Wrappers.lambdaQuery(ApplicationDO.class)
                .eq(ApplicationDO::getReceiver, requestParam.getReceiver())
                .eq(ApplicationDO::getStatus, 2)
                .eq(ApplicationDO::getDelFlag, 0);
        IPage<ApplicationDO> resultPage=baseMapper.selectPage(requestParam,queryWrapper);
        return resultPage.convert(each-> BeanUtil.toBean(each,QueryApplicationPageRespDTO.class));
    }
}
