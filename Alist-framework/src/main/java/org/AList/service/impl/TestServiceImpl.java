package org.AList.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.AList.domain.dao.entity.TestDO;
import org.AList.domain.dao.mapper.TestMapper;
import org.AList.service.TestService;
import org.springframework.stereotype.Service;

/**
 * (Test)表服务实现类
 *
 * @author makejava
 * @since 2025-04-24 16:50:44
 */
@Service("testService")
public class TestServiceImpl extends ServiceImpl<TestMapper, TestDO> implements TestService {

}

