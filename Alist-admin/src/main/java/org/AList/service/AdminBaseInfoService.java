package org.AList.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.AList.domain.dto.req.BaseClassInfoAddReqDTO;
import org.AList.domain.dto.req.BaseClassInfoListStuReqDTO;
import org.AList.domain.dto.req.BaseClassInfoUpdateReqDTO;
import org.AList.domain.dto.req.BaseMajorInfoListClassReqDTO;
import org.AList.domain.dto.resp.BaseClassInfoListStuRespDTO;
import org.AList.domain.dto.resp.BaseMajorInfoListClassRespDTO;

/**
 * 管理员基础信息操作服务类接口层
 */
public interface AdminBaseInfoService {
    /**
     * 新增班级基础信息
     * @param requestParam 新增班级信息请求体
     */
    void addBaseClassInfo(BaseClassInfoAddReqDTO requestParam);

    /**
     * 更新班级基础信息
     * @param requestParam 更新班级信息请求体
     */
    void updateBaseClassInfo(BaseClassInfoUpdateReqDTO requestParam);

    /**
     * 分页展示某个班级下的学生信息
     * @param requestParam 查询班级下面的学生请求体
     * @return 分页响应
     */
    IPage<BaseClassInfoListStuRespDTO> listClassStu(BaseClassInfoListStuReqDTO requestParam);

    /**
     * 分页展示某个专业下的班级信息
     * @param requestParam 查询专业下面的班级请求体
     * @return 分页响应
     */
    IPage<BaseMajorInfoListClassRespDTO> listMajorClass(BaseMajorInfoListClassReqDTO requestParam);
}
