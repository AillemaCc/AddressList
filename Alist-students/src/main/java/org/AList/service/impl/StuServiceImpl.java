package org.AList.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.AList.common.convention.exception.ClientException;
import org.AList.domain.dao.entity.StudentDO;
import org.AList.domain.dao.mapper.StudentMapper;
import org.AList.domain.dto.req.StuLoginReqDTO;
import org.AList.domain.dto.resp.StuLoginRespDTO;
import org.AList.service.StuService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 学生客户端接口服务实现层
 */
@Service
@RequiredArgsConstructor
public class StuServiceImpl extends ServiceImpl<StudentMapper,StudentDO> implements StuService {
    private final StudentMapper studentMapper;
    private final StringRedisTemplate stringRedisTemplate;
    @Value("${jwt.secret}")  // 自动注入配置值
    private String secretKey;
    /**
     * 用户登录接口实现类
     * @param requestParam
     * @return
     */
    @Override
    public StuLoginRespDTO login(StuLoginReqDTO requestParam) {
        LambdaQueryWrapper<StudentDO> queryWrapper = Wrappers.lambdaQuery(StudentDO.class)
                .eq(StudentDO::getStudentId, requestParam.getStudentId())
                .eq(StudentDO::getPassword, requestParam.getPassword())
                .eq(StudentDO::getDelFlag, 0)
                .eq(StudentDO::getStatus, 1);
        StudentDO studentDO=studentMapper.selectOne(queryWrapper);
        if(studentDO==null){
            throw new ClientException("学生不存在");
        }
        String uuid= UUID.randomUUID().toString();
        // 生成的uuid作为用户登录信息传入redis
        // 更清晰的键设计（使用冒号分隔）
        String redisKey = "login:student:" + requestParam.getStudentId();
        stringRedisTemplate.opsForHash().put(redisKey, uuid, JSON.toJSONString(studentDO));
        stringRedisTemplate.expire(redisKey, 30, TimeUnit.MINUTES);
        return new StuLoginRespDTO(uuid);
    }
}
