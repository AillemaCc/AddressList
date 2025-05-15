package org.AList.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.AList.common.convention.exception.ClientException;
import org.AList.common.convention.exception.ServiceException;
import org.AList.domain.dao.entity.BoardDO;
import org.AList.domain.dao.mapper.BoardMapper;
import org.AList.domain.dto.baseDTO.BoardBaseDTO;
import org.AList.domain.dto.req.BoardAddReqDTO;
import org.AList.domain.dto.req.BoardDeleteReqDTO;
import org.AList.domain.dto.req.BoardQueryReqDTO;
import org.AList.domain.dto.req.BoardUpdateReqDTO;
import org.AList.domain.dto.resp.BoardQueryRespDTO;
import org.AList.service.BoardService;
import org.springframework.stereotype.Service;

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
        validateRequestParam(requestParam);

        // 2. 检查boardId是否已存在
        if (requestParam.getBoardId() != null) {
            checkBoardIdNotExists(requestParam.getBoardId());
        }

        // 3. DTO转DO
        BoardDO boardDO = convertToBoardDO(requestParam);

        // 4. 保存到数据库
        boolean saveResult = save(boardDO);

        // 5. 检查保存结果
        if (!saveResult) {
            throw new ServiceException("公告保存失败");
        }
    }

    @Override
    public void updateBoard(BoardUpdateReqDTO requestParam) {
        Integer boardId=requestParam.getBoardId();
        // 1. 参数校验
        if (boardId == null || boardId <= 0) {
            throw new ClientException("无效的公告标识号");
        }
        validateRequestParam(requestParam);

        // 2. 根据boardId获取公告
        BoardDO existingBoard = lambdaQuery()
                .eq(BoardDO::getBoardId, boardId)
                .eq(BoardDO::getDelFlag, 0)
                .one();

        if (existingBoard == null) {
            throw new ServiceException("公告不存在或已被删除");
        }

        // 3. DTO转DO
        BoardDO boardDO = convertToBoardDO(requestParam);

        // 4. 更新数据库
        int update = boardMapper.update(boardDO, null);

        // 5. 检查更新结果
        if (update==0) {
            throw new ServiceException("公告更新失败");
        }
    }

    @Override
    public void deleteBoard(BoardDeleteReqDTO requestParam) {
        Integer boardId=requestParam.getBoardId();
        // 1. 参数校验
        if (boardId == null || boardId <= 0) {
            throw new ClientException("无效的公告标识号");
        }

        // 2. 根据boardId获取公告
        BoardDO existingBoard = lambdaQuery()
                .eq(BoardDO::getBoardId, boardId)
                .eq(BoardDO::getDelFlag, 0)
                .one();

        if (existingBoard == null) {
            throw new ServiceException("公告不存在或已被删除");
        }

        // 3. 执行删除
        boolean deleteResult = removeById(existingBoard.getId());

        // 4. 检查删除结果
        if (!deleteResult) {
            throw new ServiceException("公告删除失败");
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
        return boardPage.convert(boardDO -> BoardQueryRespDTO.builder()
                .title(boardDO.getTitle())
                .boardId(boardDO.getBoardId())
                .category(boardDO.getCategory())
                .content(boardDO.getContent())
                .status(boardDO.getStatus())
                .priority(boardDO.getPriority())
                .createTime(boardDO.getCreateTime())
                .updateTime(boardDO.getUpdateTime())
                .build());
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
        return boardPage.convert(boardDO -> BoardQueryRespDTO.builder()
                .title(boardDO.getTitle())
                .boardId(boardDO.getBoardId())
                .category(boardDO.getCategory())
                .content(boardDO.getContent())
                .status(boardDO.getStatus())
                .priority(boardDO.getPriority())
                .createTime(boardDO.getCreateTime())
                .updateTime(boardDO.getUpdateTime())
                .build());

    }

    private <T extends BoardBaseDTO> void validateRequestParam(T requestParam) {
        if (requestParam == null) {
            throw new ClientException("请求参数不能为空");
        }
        if (StringUtils.isBlank(requestParam.getTitle())) {
            throw new ClientException("公告标题不能为空");
        }
        if (StringUtils.isBlank(requestParam.getContent())) {
            throw new ClientException("公告内容不能为空");
        }
        if (requestParam.getStatus() == null) {
            throw new ClientException("状态不能为空");
        }
    }

    private void checkBoardIdNotExists(Integer boardId) {
        boolean exists = lambdaQuery()
                .eq(BoardDO::getBoardId, boardId)
                .exists();
        if (exists) {
            throw new ServiceException("公告标识号已存在");
        }
    }

    private BoardDO convertToBoardDO(BoardAddReqDTO requestParam) {
        return convertRequestToBoardDO(requestParam);
    }

    private BoardDO convertToBoardDO(BoardUpdateReqDTO requestParam) {
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
}
