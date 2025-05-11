package org.AList.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.AList.common.convention.exception.ClientException;
import org.AList.domain.dao.entity.ClassInfoDO;
import org.AList.domain.dao.mapper.ClassInfoMapper;
import org.AList.domain.dto.req.BaseClassInfoAddReqDTO;
import org.AList.service.AdminBaseInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 管理员基础信息操作服务接口实现层
 */
@Service
@RequiredArgsConstructor
public class AdminBaseInfoServiceImpl implements AdminBaseInfoService {
    private final ClassInfoMapper classInfoMapper;
    /**
     * 新增班级基础信息
     *
     * @param requestParam 新增班级信息请求体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBaseClassInfo(BaseClassInfoAddReqDTO requestParam) {
        Objects.requireNonNull(requestParam, "请求参数不能为空");
        if (requestParam.getClassNum() == null || StringUtils.isBlank(requestParam.getClassName())) {
            throw new ClientException("班级编号和名称不能为空");
        }
        Integer classNum = requestParam.getClassNum();
        String className = requestParam.getClassName();
        LambdaQueryWrapper<ClassInfoDO> uniqueWrapper = Wrappers.lambdaQuery(ClassInfoDO.class)
                .eq(ClassInfoDO::getClassNum, classNum)
                .eq(ClassInfoDO::getClassName, className)
                .eq(ClassInfoDO::getDelFlag, 0);
        ClassInfoDO uniqueDO = classInfoMapper.selectOne(uniqueWrapper);
        if (uniqueDO != null) {
            throw new ClientException("新增的班级信息已存在，请不要重复添加");
        }
        ClassInfoDO classInfoDO =ClassInfoDO.builder()
                .className(className)
                .classNum(classNum)
                .build();
        int insert = classInfoMapper.insert(classInfoDO);
        if (insert!=1){
            throw new ClientException("新增异常，请重试");
        }
    }
}
