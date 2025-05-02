package org.AList.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 元数据处理自动填充类
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入情况下的元数据填充
     * @param metaObject 元数据
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("开始插入填充...");
        this.strictInsertFill(metaObject, "createTime", Date::new, Date.class);
        this.strictInsertFill(metaObject, "updateTime", Date::new, Date.class);
        this.strictInsertFill(metaObject, "delFlag", Integer.class, 0);
    }

    /**
     * 更新情况下的元数据填充
     * @param metaObject 元数据
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("开始更新填充...");
        this.strictUpdateFill(metaObject, "updateTime", Date::new, Date.class);
    }
}