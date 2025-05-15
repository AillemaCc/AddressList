package org.AList.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.*;
import org.AList.domain.dto.resp.BoardQueryRespDTO;
import org.AList.service.BoardService;
import org.springframework.web.bind.annotation.*;

/**
 * 公告板控制层
 */
@RestController
@RequestMapping("/api/admin/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /**
     * 新增公告--默认保存为草稿
     */
    @PutMapping("/add")
    public Result<Void> addBoard(@RequestBody BoardAddReqDTO requestParam){
        boardService.addBoard(requestParam);
        return Results.success();
    }

    /**
     * 修改公告
     */
    @PostMapping("/update")
    public Result<Void> updateBoard(@RequestBody BoardUpdateReqDTO requestParam){
        boardService.updateBoard(requestParam);
        return Results.success();
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/delete")
    public Result<Void> deleteBoard(@RequestBody BoardDeleteReqDTO requestParam){
        boardService.deleteBoard(requestParam);
        return Results.success();
    }

    /**
     * 分页查询所有未删除公告
     */
    @GetMapping("/queryAllValid")
    public Result<IPage<BoardQueryRespDTO>> queryAllValid(@RequestBody BoardQueryReqDTO requestParam){
        return Results.success(boardService.queryAllValid(requestParam));
    }

    /**
     * 分页查询所有已删除公告
     */
    @GetMapping("/queryAllDeleted")
    public Result<IPage<BoardQueryRespDTO>> queryAllDeleted(@RequestBody BoardQueryReqDTO requestParam){
        return Results.success(boardService.queryAllDeleted(requestParam));
    }

    /**
     * 根据公告标识号发布草稿
     */
    @PostMapping("/release")
    public Result<Void> releaseBoard(@RequestBody BoardReleaseReqDTO requestParam){
        boardService.releaseBoard(requestParam);
        return Results.success();
    }

    /**
     * 根据公告标识号恢复已删除公告
     */
    @PostMapping("/restore")
    public Result<Void> restoreBoard(@RequestBody BoardRestoreReqDTO requestParam){
        boardService.restoreBoard(requestParam);
        return Results.success();
    }

    /**
     * 根据公告标识号下架公告
     */
    @PostMapping("/pullOff")
    public Result<Void> pullOffBoard(@RequestBody BoardPullOffReqDTO requestParam){
        boardService.pullOffBoard(requestParam);
        return Results.success();
    }


}
