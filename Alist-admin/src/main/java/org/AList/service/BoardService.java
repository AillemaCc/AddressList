package org.AList.service;

import org.AList.domain.dto.req.BoardAddReqDTO;
import org.AList.domain.dto.req.BoardDeleteReqDTO;
import org.AList.domain.dto.req.BoardUpdateReqDTO;

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
}
