package org.AList.domain.dao.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.AList.annonation.EncryptField;

/**
 * (Test)表实体类
 *
 * @author makejava
 * @since 2025-04-24 16:52:37
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("test")
@Builder
public class TestDO {

    private Integer id;

    @EncryptField
    private String name;

}

