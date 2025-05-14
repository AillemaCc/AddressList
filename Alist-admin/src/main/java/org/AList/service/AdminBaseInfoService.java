package org.AList.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.AList.domain.dto.req.*;
import org.AList.domain.dto.resp.BaseAcademyInfoListMajorRespDTO;
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

    /**
     * 分页展示某个学院下的班级信息
     * @param requestParam 查询学院下面的专业请求体
     * @return 分页相应
     */
    IPage<BaseAcademyInfoListMajorRespDTO> listAcademyMajor(BaseAcademyInfoListMajorReqDTO requestParam);

    /**
     * 更新班级所属的专业和学院信息，也就是班级信息的调整
     * @param requestParam 请求参数
     */
    void updateBaseClassInfoMA(BaseClassInfoUpdateMAReqDTO requestParam);

    /**
     * 更新专业基础信息
     * @param requestParam 请求参数
     */
    void updateBaseMajorInfo(BaseMajorInfoUpdateReqDTO requestParam);
}
