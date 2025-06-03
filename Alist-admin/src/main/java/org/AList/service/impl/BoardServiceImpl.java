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
import lombok.extern.slf4j.Slf4j;
import org.AList.common.convention.exception.ServiceException;
import org.AList.common.convention.exception.UserException;
import org.AList.domain.dao.entity.BoardDO;
import org.AList.domain.dao.mapper.BoardMapper;
import org.AList.domain.dto.baseDTO.BoardBaseDTO;
import org.AList.domain.dto.req.*;
import org.AList.domain.dto.resp.BoardQueryRespDTO;
import org.AList.service.BoardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.AList.common.convention.errorcode.BaseErrorCode.*;

/**
 * 公告板服务层实现层
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BoardServiceImpl extends ServiceImpl<BoardMapper, BoardDO> implements BoardService {

    private final BoardMapper boardMapper;
    private static final Logger logger = LoggerFactory.getLogger(BoardServiceImpl.class);

    @Override
    @Transactional(rollbackFor = Exception.class)
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
    @Transactional(rollbackFor = Exception.class)
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
    @Transactional(rollbackFor = Exception.class)
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
        // 2. 构建更新条件
        LambdaUpdateWrapper<BoardDO> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(BoardDO::getBoardId, boardId)
                .eq(BoardDO::getDelFlag, 0)
                .set(BoardDO::getStatus, 1);

        int update = boardMapper.update(null, wrapper);

        if (update == 0) {
            throw new ServiceException(ANNOUNCE_PUBLISH_FAIL); // C0376：处理的公告发布失败
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

    @Override
    public BoardQueryRespDTO queryBoardById(BoardQueryByIdReqDTO requestParam) {
        logger.info("开始查询公告详情，请求参数: {}", requestParam);

        Integer boardId = requestParam.getBoardId();

        // 1. 参数校验
        if (boardId == null || boardId <= 0) {
            logger.warn("公告ID无效，ID值为: {}", boardId);
            throw new UserException(INVALID_ANNOUNCE_ID); // A0002：请求参数为空
        }

        // 2. 根据boardId查询公告
        logger.debug("根据公告ID查询数据，boardId: {}", boardId);
        BoardDO existingBoard = lambdaQuery()
                .eq(BoardDO::getBoardId, boardId)
                .eq(BoardDO::getDelFlag, 0)
                .one();

        if (existingBoard == null) {
            logger.warn("未找到指定公告，boardId: {}", boardId);
            throw new ServiceException(ANNOUNCE_NOT_FOUND); // 公告不存在
        }

        // 3. 转换为响应DTO并返回
        logger.debug("公告数据查询成功，boardId: {}", boardId);
        return convertToBoardQueryRespDTO(existingBoard);
    }

    private <T extends BoardBaseDTO> void validateAOURequestParam(T requestParam) {
        logger.info("开始校验请求参数");

        if (requestParam == null) {
            logger.warn("请求参数为空");
            throw new UserException(EMPTY_PARAM); // A0002：请求参数为空
        }

        if (StringUtils.isBlank(requestParam.getTitle())) {
            logger.warn("公告标题为空");
            throw new UserException(ANNOUNCE_EMPTY_TITLE); // A0704：公告标题为空
        }

        if (StringUtils.isBlank(requestParam.getContent())) {
            logger.warn("公告内容为空");
            throw new UserException(ANNOUNCE_EMPTY_CONTENT); // A0703：公告内容为空
        }

        if (requestParam.getStatus() == null) {
            logger.warn("公告状态为空");
            throw new UserException(ANNOUNCE_EMPTY_STATUS); // A0705：公告状态为空
        }

        logger.info("请求参数校验通过");
    }

    private void checkBoardIdNotExists(Integer boardId) {
        logger.info("检查公告ID是否已存在，boardId: {}", boardId);

        boolean exists = lambdaQuery()
                .eq(BoardDO::getBoardId, boardId)
                .exists();

        if (exists) {
            logger.warn("公告ID已存在，boardId: {}", boardId);
            throw new UserException(ANNOUNCE_ID_EXIST); // A0702：公告标识号已存在
        }
    }

    private Integer generateBoardId() {
        logger.info("开始生成新的公告ID");

        // 使用当前时间戳的后8位数字作为基础ID
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(DateTimeFormatter.ofPattern("yyMMdd"));

        logger.debug("日期前缀: {}", dateStr);

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
            logger.error("当日公告已达上限，无法生成新ID");
            throw new ServiceException(ANNOUNCE_DAILY_LIMIT); // A0706：当日公告已达上限
        }

        String generatedId = dateStr + String.format("%03d", sequence);
        logger.info("生成的新公告ID为: {}", generatedId);
        return Integer.valueOf(generatedId);
    }

    private BoardDO convertToBoardDO(BoardAddReqDTO requestParam) {
        logger.debug("将新增公告请求转换为DO对象");
        return convertRequestToBoardDO(requestParam);
    }

    private BoardDO convertRequestToBoardDO(BoardBaseDTO requestParam) {
        logger.debug("构建公告DO对象，title: {}, category: {}", requestParam.getTitle(), requestParam.getCategory());
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
        logger.debug("将公告DO对象转换为响应DTO，title: {}", boardDO.getTitle());
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
