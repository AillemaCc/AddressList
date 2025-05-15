package org.AList.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.AList.common.convention.result.Result;
import org.AList.common.convention.result.Results;
import org.AList.domain.dto.req.BoardAddReqDTO;
import org.AList.domain.dto.req.BoardDeleteReqDTO;
import org.AList.domain.dto.req.BoardQueryAllValidReqDTO;
import org.AList.domain.dto.req.BoardUpdateReqDTO;
import org.AList.domain.dto.resp.BoardQueryAllValidRespDTO;
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
     * 新增公告
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
    public Result<IPage<BoardQueryAllValidRespDTO>> queryAllValid(@RequestBody BoardQueryAllValidReqDTO requestParam){
        return Results.success(boardService.queryAllValid(requestParam));

    }
}
