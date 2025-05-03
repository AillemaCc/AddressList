package org.AList.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.AList.domain.dao.entity.RegisterDO;
import org.AList.domain.dao.mapper.RegisterMapper;
import org.AList.domain.dto.resp.AuditUserPageRespDTO;
import org.AList.service.AdministerAuditService;
import org.springframework.stereotype.Service;

/**
 * 管理员审核相关接口实现层
 */
@Service
@RequiredArgsConstructor
public class AdministerAuditServiceImpl extends ServiceImpl<RegisterMapper, RegisterDO> implements AdministerAuditService {

    /**
     * @return 待审核用户列表
     */
    @Override
    public IPage<AuditUserPageRespDTO> listAuditUser() {
        LambdaQueryWrapper<RegisterDO> queryWrapper = Wrappers.lambdaQuery(RegisterDO.class)
                .eq(RegisterDO::getStatus, 0)
                .eq(RegisterDO::getDelFlag, 0)
                .orderByDesc(RegisterDO::getCreateTime);
        IPage<RegisterDO> resultPage=baseMapper.selectPage(new Page<>(), queryWrapper);
        return resultPage.convert(each -> BeanUtil.toBean(each,AuditUserPageRespDTO.class));
    }
}
