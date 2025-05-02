package org.AList.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.AList.common.constant.RedisCacheConstant;
import org.AList.common.convention.exception.ClientException;
import org.AList.domain.dao.entity.RegisterDO;
import org.AList.domain.dao.entity.StudentDO;
import org.AList.domain.dao.entity.StudentDefaultInfoDO;
import org.AList.domain.dao.mapper.RegisterMapper;
import org.AList.domain.dao.mapper.StudentDefaultInfoMapper;
import org.AList.domain.dao.mapper.StudentMapper;
import org.AList.domain.dto.req.StuLoginReqDTO;
import org.AList.domain.dto.req.StuRegisterReqDTO;
import org.AList.domain.dto.resp.StuLoginRespDTO;
import org.AList.service.StuService;
import org.AList.service.bloom.StudentIdBloomFilterService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.AList.common.enums.UserErrorCodeEnum.*;

/**
 * 学生客户端接口服务实现层
 */
@Service
@RequiredArgsConstructor
public class StuServiceImpl extends ServiceImpl<StudentMapper,StudentDO> implements StuService {
    private final StudentMapper studentMapper;
    private final RegisterMapper registerMapper;
    private final StudentDefaultInfoMapper studentDefaultInfoMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final StudentIdBloomFilterService studentIdBloomFilterService;
    private final RedissonClient redissonClient;
    /**
     * 用户登录接口实现类
     * @param requestParam 用户登录请求实体
     * @return 用户登录响应实体--token
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
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey("login:student:"+requestParam.getStudentId()))){
            throw new ClientException("学生已登录");
        }
        String uuid= UUID.randomUUID().toString();
        // 生成的uuid作为用户登录信息传入redis
        // 更清晰的键设计（使用冒号分隔）
        String redisKey = "login:student:" + requestParam.getStudentId();
        stringRedisTemplate.opsForHash().put(redisKey, uuid, JSON.toJSONString(studentDO));
        stringRedisTemplate.expire(redisKey, 30, TimeUnit.MINUTES);
        return new StuLoginRespDTO(uuid);
    }

    /**
     * 检查用户是否登录
     * @param studentId 学号
     * @param token 登录生成的token
     * @return 是否登录的结果
     */
    @Override
    public Boolean checkLogin(String studentId, String token) {
        return stringRedisTemplate.opsForHash().get("login:student:"+studentId,token)!=null;
    }

    /**
     * 用户登出
     * @param studentId 学号
     * @param token 用户登录产生的token
     */
    @Override
    public void logout(String studentId, String token) {
        if(checkLogin(studentId,token)){
            stringRedisTemplate.delete("login:student:"+studentId);
            return;
        }
        throw new ClientException("用户token不存在或者用户未登录");
    }

    /**
     * 用户注册接口
     *
     * @param requestParam 用户注册请求实体
     * @return 用户注册返回的唯一key
     */
    @Override
    public String register(StuRegisterReqDTO requestParam) {
        if(!studentIdBloomFilterService.contain(requestParam.getStudentId())){
            throw new ClientException(USER_NULL);
        }
        LambdaQueryWrapper<StudentDefaultInfoDO> queryWrapper = Wrappers.lambdaQuery(StudentDefaultInfoDO.class)
                .eq(StudentDefaultInfoDO::getStudentId, requestParam.getStudentId())
                .eq(StudentDefaultInfoDO::getDelFlag, 0);
        if(Objects.isNull(studentDefaultInfoMapper.selectOne(queryWrapper))){
            throw new ClientException(USER_NULL);
        }
        RLock rLock=redissonClient.getLock(RedisCacheConstant.LOCK_STUDENT_REGISTER_KEY+requestParam.getStudentId());
        try{
            if(rLock.tryLock()){
                try{
                    String uuid= UUID.randomUUID().toString();
                    RegisterDO registerDO=RegisterDO.builder()
                            .studentId(requestParam.getStudentId())
                            .name(requestParam.getName())
                            .phone(requestParam.getPhone())
                            .email(requestParam.getEmail())
                            .password(requestParam.getPassword())
                            .status(0)
                            .registerToken(uuid)
                            .build();
                    int insert= registerMapper.insert(registerDO);
                    if (insert < 1) {
                        throw new ClientException(USER_SAVE_ERROR);
                    }
                    return uuid;
                }catch (DuplicateKeyException e){
                    throw new ClientException(USER_EXIST);
                }

            }
            throw new ClientException(USER_EXIST);
        }
        finally {
            rLock.unlock();
        }
    }
}
