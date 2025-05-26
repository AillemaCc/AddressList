package org.AList.domain.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.AList.domain.dao.entity.OperLogDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;

public interface OperLogMapper extends BaseMapper<OperLogDO> {

    /**
     * 批量删除指定时间之前的日志
     */
    @Delete("DELETE FROM t_oper_log WHERE oper_time < #{cutoffTime} LIMIT #{batchSize}")
    int deleteLogsBefore(@Param("cutoffTime") Date cutoffTime, @Param("batchSize") int batchSize);

    /**
     * 统计指定时间之前的日志数量
     */
    @Select("SELECT COUNT(*) FROM t_oper_log WHERE oper_time < #{cutoffTime}")
    long countLogsBefore(@Param("cutoffTime") Date cutoffTime);
}
