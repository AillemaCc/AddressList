package org.AList.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.AList.common.convention.exception.ServiceException;
import org.AList.common.convention.exception.UserException;
import org.AList.domain.dao.entity.BoardDO;
import org.AList.domain.dao.mapper.BoardMapper;
import org.AList.domain.dto.baseDTO.BoardBaseDTO;
import org.AList.domain.dto.req.*;
import org.AList.domain.dto.resp.BoardQueryRespDTO;
import org.AList.service.BoardService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.AList.common.convention.errorcode.BaseErrorCode.*;

/**
 * 公告板服务层实现层
 */
@Service
@RequiredArgsConstructor
public class BoardServiceImpl extends ServiceImpl<BoardMapper, BoardDO> implements BoardService {

    private final BoardMapper boardMapper;

    @Override
    public void addBoard(BoardAddReqDTO requestParam) {
        // 1. 参数校验
        validateAOURequestParam(requestParam);

        // 2. 如果boardId为空，自动生成
        if (requestParam.getBoardId() == null) {
            requestParam.setBoardId(generateBoardId());
        } else {
            // 如果提供了boardId，检查是否已存在
            checkBoardIdNotExists(requestParam.getBoardId());
        }

        // 3. DTO转DO
        BoardDO boardDO = convertToBoardDO(requestParam);

        // 4. 保存到数据库
        boolean saveResult = save(boardDO);

        // 5. 检查保存结果
        if (!saveResult) {
            throw new ServiceException(ANNOUNCE_SAVE_FAIL);                                                             //C0371：处理的公告保存失败
        }
    }

    @Override
    public void updateBoard(BoardUpdateReqDTO requestParam) {
        Integer boardId=requestParam.getBoardId();
        // 1. 参数校验
        if (boardId == null || boardId <= 0) {
            throw new UserException(INVALID_ANNOUNCE_ID);                                                               //A0701：无效的公告标识号
        }
        validateAOURequestParam(requestParam);

        // 2. 根据boardId获取公告
        BoardDO existingBoard = lambdaQuery()
                .eq(BoardDO::getBoardId, boardId)
                .eq(BoardDO::getDelFlag, 0)
                .one();

        if (existingBoard == null) {
            throw new ServiceException(ANNOUNCE_NOT_FOUND);                                                             //C0373：处理的公告不存在或已删除
        }

        // 3. 使用 LambdaUpdateWrapper 精确更新，排除 boardId
        LambdaUpdateWrapper<BoardDO> updateWrapper = Wrappers.lambdaUpdate(BoardDO.class)
                .eq(BoardDO::getBoardId, boardId)
                .eq(BoardDO::getDelFlag, 0)
                .set(BoardDO::getTitle, requestParam.getTitle())
                .set(BoardDO::getCategory, requestParam.getCategory())
                .set(BoardDO::getContent, requestParam.getContent())
                .set(BoardDO::getStatus, requestParam.getStatus())
                .set(BoardDO::getPriority, requestParam.getPriority())
                .set(BoardDO::getCoverImage, requestParam.getCoverImage());

        // 4. 执行更新
        boolean updateResult = update(updateWrapper);

        // 5. 检查更新结果
        if (!updateResult) {
            throw new ServiceException(ANNOUNCE_UPDATE_FAIL);
        }
    }

    @Override
    public void deleteBoard(BoardDeleteReqDTO requestParam) {
        Integer boardId=requestParam.getBoardId();
        // 1. 参数校验
        if (boardId == null || boardId <= 0) {
            throw new UserException(INVALID_ANNOUNCE_ID);                                                               //A0701：无效的公告标识号
        }

        // 2. 根据boardId获取公告
        BoardDO existingBoard = lambdaQuery()
                .eq(BoardDO::getBoardId, boardId)
                .eq(BoardDO::getDelFlag, 0)
                .one();

        if (existingBoard == null) {
            throw new ServiceException(ANNOUNCE_NOT_FOUND);                                                             //C0373：处理的公告不存在或已删除
        }

        // 3. 执行删除
        boolean deleteResult = removeById(existingBoard.getId());

        // 4. 检查删除结果
        if (!deleteResult) {
            throw new ServiceException(ANNOUNCE_DEL_FAIL);                                                              //C0374：处理的公告删除失败
        }
    }

    /**
     * 分页查询所有未删除公告
     */
    @Override
    public IPage<BoardQueryRespDTO> queryAllValid(BoardQueryReqDTO requestParam) {
        int current=requestParam.getCurrent()==null?0:requestParam.getCurrent();
        int size=requestParam.getSize()==null?10:requestParam.getSize();
        LambdaQueryWrapper<BoardDO> queryWrapper = Wrappers.lambdaQuery(BoardDO.class)
                .eq(BoardDO::getDelFlag, 0)
                .orderByDesc(BoardDO::getStatus)
                .orderByDesc(BoardDO::getPriority) // 按优先级降序
                .orderByDesc(BoardDO::getCreateTime);// 再按创建时间降序
        IPage<BoardDO> boardPage=page(new Page<>(current,size),queryWrapper);
        return boardPage.convert(this::convertToBoardQueryRespDTO);
    }

    /**
     * 分页查询所有已删除公告
     */
    @Override
    public IPage<BoardQueryRespDTO> queryAllDeleted(BoardQueryReqDTO requestParam) {
        // 1. 参数处理
        int current = requestParam.getCurrent() == null || requestParam.getCurrent() <= 0 ? 1 : requestParam.getCurrent();
        int size = requestParam.getSize() == null || requestParam.getSize() <= 0 ? 10 : requestParam.getSize();

        // 2. 创建分页对象并设置排序
        Page<BoardDO> page = new Page<>(current, size);
        page.addOrder(OrderItem.desc("status"));      // 按状态降序
        page.addOrder(OrderItem.desc("priority"));    // 按优先级降序
        page.addOrder(OrderItem.desc("create_time")); // 按创建时间降序

        // 3. 调用Mapper方法（结合@Select注解）
        IPage<BoardDO> boardPage = boardMapper.selectByDelFlag(page, 0); // delFlag=0表示未删除

        // 4. 转换为响应DTO
        return boardPage.convert(this::convertToBoardQueryRespDTO);

    }

    /**
     * 分页查询所有已发布公告
     */
    @Override
    public IPage<BoardQueryRespDTO> queryAllReleased(BoardQueryReqDTO requestParam) {
        int current=requestParam.getCurrent()==null?0:requestParam.getCurrent();
        int size=requestParam.getSize()==null?10:requestParam.getSize();
        LambdaQueryWrapper<BoardDO> queryWrapper = Wrappers.lambdaQuery(BoardDO.class)
                .eq(BoardDO::getDelFlag, 0)
                .eq(BoardDO::getStatus, 1)
                .orderByDesc(BoardDO::getPriority) // 按优先级降序
                .orderByDesc(BoardDO::getCreateTime);// 再按创建时间降序
        IPage<BoardDO> boardPage=page(new Page<>(current,size),queryWrapper);
        return boardPage.convert(this::convertToBoardQueryRespDTO);
    }

    /**
     * 根据公告标识号发布草稿
     */
    @Override
    public void releaseBoard(BoardReleaseReqDTO requestParam) {
        Integer boardId=requestParam.getBoardId();
        // 1. 参数校验
        if (boardId == null || boardId <= 0) {
            throw new UserException(INVALID_ANNOUNCE_ID);                                                               //A0701：无效的公告标识号
        }
        // 2. 根据boardId获取公告
        BoardDO existingBoard = lambdaQuery()
                .eq(BoardDO::getBoardId, boardId)
                .eq(BoardDO::getDelFlag, 0)
                .one();

        if (existingBoard == null) {
            throw new ServiceException(ANNOUNCE_NOT_FOUND);                                                             //C0373：处理的公告不存在或已删除
        }
        LambdaUpdateWrapper<BoardDO> set = Wrappers.lambdaUpdate(BoardDO.class)
                .set(BoardDO::getStatus, 1);
        int update = boardMapper.update(existingBoard, set);
        if (update==0) {
            throw new ServiceException(ANNOUNCE_PUBLISH_FAIL);                                                          //C0376：处理的公告发布失败
        }

    }

    /**
     * 根据公告标识号恢复已删除公告
     */
    @Override
    public void restoreBoard(BoardRestoreReqDTO requestParam) {
        Integer boardId=requestParam.getBoardId();
        // 1. 参数校验
        if (boardId == null || boardId <= 0) {
            throw new UserException(INVALID_ANNOUNCE_ID);                                                               //A0701：无效的公告标识号
        }
        BoardDO restoredBoard = boardMapper.selectRestoreByBoardId(1,requestParam.getBoardId());
        if (restoredBoard == null) {
            throw new ServiceException(ANNOUNCE_NOT_FOUND);                                                             //C0373：处理的公告不存在或已删除
        }
        restoredBoard.setDelFlag(0);
        int update = boardMapper.restoreBoard(restoredBoard.getBoardId());
        if (update==0) {
            throw new ServiceException(ANNOUNCE_RESTORE_FAIL);                                                          //C0375,处理的公告恢复失败
        }
    }

    /**
     * 根据公告标识号下架公告
     */
    @Override
    public void pullOffBoard(BoardPullOffReqDTO requestParam) {
        Integer boardId=requestParam.getBoardId();
        // 1. 参数校验
        if (boardId == null || boardId <= 0) {
            throw new UserException(INVALID_ANNOUNCE_ID);                                                               //A0701：无效的公告标识号
        }

        // 2. 根据boardId获取公告
        BoardDO existingBoard = lambdaQuery()
                .eq(BoardDO::getBoardId, boardId)
                .eq(BoardDO::getDelFlag, 0)
                .eq(BoardDO::getStatus, 1)
                .one();
        if (existingBoard == null) {
            throw new ServiceException(ANNOUNCE_NOT_FOUND);                                                             //C0373：处理的公告不存在或已删除
        }
        existingBoard.setStatus(2);
        int update = boardMapper.update(existingBoard, null);
        if (update==0) {
            throw new ServiceException(ANNOUNCE_OFFLINE_FAIL);                                                          //C0377：处理的公告下架失败
        }
    }

    /**
     * 分页查询所有已下架公告
     */
    @Override
    public IPage<BoardQueryRespDTO> queryAllPullOff(BoardQueryReqDTO requestParam) {
        int current=requestParam.getCurrent()==null?0:requestParam.getCurrent();
        int size=requestParam.getSize()==null?10:requestParam.getSize();
        LambdaQueryWrapper<BoardDO> queryWrapper = Wrappers.lambdaQuery(BoardDO.class)
                .eq(BoardDO::getDelFlag, 0)
                .eq(BoardDO::getStatus, 2)
                .orderByDesc(BoardDO::getPriority) // 按优先级降序
                .orderByDesc(BoardDO::getCreateTime);// 再按创建时间降序
        IPage<BoardDO> boardPage=page(new Page<>(current,size),queryWrapper);
        return boardPage.convert(this::convertToBoardQueryRespDTO);
    }

    /**
     * 分页查询所有草稿
     */
    @Override
    public IPage<BoardQueryRespDTO> queryAllDraft(BoardQueryReqDTO requestParam) {
        int current=requestParam.getCurrent()==null?0:requestParam.getCurrent();
        int size=requestParam.getSize()==null?10:requestParam.getSize();
        LambdaQueryWrapper<BoardDO> queryWrapper = Wrappers.lambdaQuery(BoardDO.class)
                .eq(BoardDO::getDelFlag, 0)
                .eq(BoardDO::getStatus, 0)
                .orderByDesc(BoardDO::getPriority) // 按优先级降序
                .orderByDesc(BoardDO::getCreateTime);// 再按创建时间降序
        IPage<BoardDO> boardPage=page(new Page<>(current,size),queryWrapper);
        return boardPage.convert(this::convertToBoardQueryRespDTO);
    }

    private <T extends BoardBaseDTO> void validateAOURequestParam(T requestParam) {
        if (requestParam == null) {
            throw new UserException(EMPTY_PARAM);                                                                       //A0002：请求参数为空
        }
        if (StringUtils.isBlank(requestParam.getTitle())) {
            throw new UserException(ANNOUNCE_EMPTY_TITLE);                                                              //A0704：公告标题为空
        }
        if (StringUtils.isBlank(requestParam.getContent())) {
            throw new UserException(ANNOUNCE_EMPTY_CONTENT);                                                            //A0703：公告内容为空
        }
        if (requestParam.getStatus() == null) {
            throw new UserException(ANNOUNCE_EMPTY_STATUS);                                                             //A0705：公告状态为空
        }
    }

    private void checkBoardIdNotExists(Integer boardId) {
        boolean exists = lambdaQuery()
                .eq(BoardDO::getBoardId, boardId)
                .exists();
        if (exists) {
            throw new UserException(ANNOUNCE_ID_EXIST);                                                                 //A0702：公告标识号已存在
        }
    }

    private Integer generateBoardId() {
        // 使用当前时间戳的后8位数字作为基础ID
        // 格式：YYYYMMDD + 3位序号 (如: 2025052201)
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 查询当天已有的最大序号
        Integer maxTodayId = boardMapper.selectMaxBoardIdByPrefix(dateStr);

        int sequence = 1;
        if (maxTodayId != null) {
            // 提取序号部分并加1
            String maxIdStr = String.valueOf(maxTodayId);
            if (maxIdStr.length() > 8) {
                sequence = Integer.parseInt(maxIdStr.substring(8)) + 1;
            }
        }

        // 确保序号不超过3位数（最大999）
        if (sequence > 999) {
            throw new ServiceException("当日公告数量已达上限");
        }

        return Integer.valueOf(dateStr + String.format("%03d", sequence));
    }

    private BoardDO convertToBoardDO(BoardAddReqDTO requestParam) {
        return convertRequestToBoardDO(requestParam);
    }

    private BoardDO convertRequestToBoardDO(BoardBaseDTO requestParam) {
        return BoardDO.builder()
                .boardId(requestParam.getBoardId())
                .title(requestParam.getTitle())
                .category(requestParam.getCategory())
                .content(requestParam.getContent())
                .status(requestParam.getStatus())
                .priority(requestParam.getPriority())
                .coverImage(requestParam.getCoverImage())
                .build();
    }

    private BoardQueryRespDTO convertToBoardQueryRespDTO(BoardDO boardDO) {
        return BoardQueryRespDTO.builder()
                .title(boardDO.getTitle())
                .boardId(boardDO.getBoardId())
                .coverImage(boardDO.getCoverImage())
                .category(boardDO.getCategory())
                .content(boardDO.getContent())
                .status(boardDO.getStatus())
                .priority(boardDO.getPriority())
                .createTime(boardDO.getCreateTime())
                .updateTime(boardDO.getUpdateTime())
                .build();
    }

}
