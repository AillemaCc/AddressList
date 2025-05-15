package org.AList.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.AList.domain.dto.req.*;
import org.AList.domain.dto.resp.BoardQueryRespDTO;

/**
 * 公告板服务层
 */
public interface BoardService {
    /**
     * 新增公告
     */
    void addBoard(BoardAddReqDTO requestParam);

    /**
     * 修改公告
     */
    void updateBoard(BoardUpdateReqDTO requestParam);

    /**
     * 删除公告
     */
    void deleteBoard(BoardDeleteReqDTO requestParam);

    /**
     * 分页查询所有未删除公告
     */
    IPage<BoardQueryRespDTO> queryAllValid(BoardQueryReqDTO requestParam);

    /**
     * 分页查询所有已删除公告
     */
    IPage<BoardQueryRespDTO> queryAllDeleted(BoardQueryReqDTO requestParam);

    /**
     * 根据公告标识号发布草稿
     */
    void releaseBoard(BoardReleaseReqDTO requestParam);
}
