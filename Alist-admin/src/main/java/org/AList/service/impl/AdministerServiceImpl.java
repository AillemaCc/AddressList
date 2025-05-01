package org.AList.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.AList.common.convention.exception.ClientException;
import org.AList.domain.dao.entity.AdministerDO;
import org.AList.domain.dao.mapper.AdministerMapper;
import org.AList.domain.dto.req.AdminLoginReqDTO;
import org.AList.domain.dto.resp.AdminLoginRespDTO;
import org.AList.service.AdministerService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 管理员服务接口实现层
 */
@Service
@RequiredArgsConstructor
public class AdministerServiceImpl extends ServiceImpl<AdministerMapper, AdministerDO> implements AdministerService {
    private final AdministerMapper administerMapper;
    private final StringRedisTemplate stringRedisTemplate;
    /**
     * 管理员登录实现方法
     * @param requestParam 管理员登录请求实体
     * @return 管理员登录响应实体
     */
    @Override
    public AdminLoginRespDTO login(AdminLoginReqDTO requestParam) {
        LambdaQueryWrapper<AdministerDO> queryWrapper = Wrappers.lambdaQuery(AdministerDO.class)
                .eq(AdministerDO::getUsername, requestParam.getUsername())
                .eq(AdministerDO::getPassword, requestParam.getPassword())
                .eq(AdministerDO::getDelFlag, 0);
        AdministerDO administerDO=baseMapper.selectOne(queryWrapper);
        if(administerDO==null){
            throw new ClientException("管理员账户不存在");
        }
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey("login:administer"+requestParam.getUsername()))){
            throw new ClientException("管理员已登录");
        }
        String redisKey="login:administer"+requestParam.getUsername();
        String uuid= UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put(redisKey,uuid, JSON.toJSONString(administerDO));
        stringRedisTemplate.expire(redisKey,30, TimeUnit.MINUTES);
        return new AdminLoginRespDTO(uuid);
    }
}
