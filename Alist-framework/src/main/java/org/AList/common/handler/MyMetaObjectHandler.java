package org.AList.common.handler;

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

        // 仅在字段值为 null 时填充（避免覆盖已有值）
        if (metaObject.getValue("createTime") == null) {
            this.strictInsertFill(metaObject, "createTime", Date::new, Date.class);
        }
        if (metaObject.getValue("updateTime") == null) {
            this.strictInsertFill(metaObject, "updateTime", Date::new, Date.class);
        }
        if (metaObject.getValue("delFlag") == null) {  // 仅当 delFlag 未设置时才填充默认值 0
            this.strictInsertFill(metaObject, "delFlag", Integer.class, 0);
        }
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