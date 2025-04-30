package org.AList.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.AList.domain.dao.entity.Test;
import org.AList.domain.dao.mapper.TestMapper;
import org.AList.service.TestService;
import org.springframework.stereotype.Service;

/**
 * 测试接口实现类
 */
@Service("testService")
public class TestServiceImpl extends ServiceImpl<TestMapper, Test> implements TestService {

}

