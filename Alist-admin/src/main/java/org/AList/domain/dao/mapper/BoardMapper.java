package org.AList.domain.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.AList.domain.dao.entity.BoardDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 公告表持久层
 */
public interface BoardMapper extends BaseMapper<BoardDO> {

    @Select("SELECT * FROM t_bulletin_board WHERE del_flag=#{delFlag}")
    IPage<BoardDO> selectByDelFlag(Page<BoardDO> page, @Param("delFlag") Integer delFlag);

    @Select("SELECT * FROM t_bulletin_board WHERE del_flag=#{delFlag} AND board_id=#{boardId}")
    BoardDO selectRestoreByBoardId(@Param("delFlag") Integer delFlag,@Param("boardId") Integer boardId);

    @Update("UPDATE t_bulletin_board SET del_flag = 0 WHERE board_id=#{boardId} AND del_flag = 1 ")
    int restoreBoard(@Param("boardId") Integer boardId);
}
