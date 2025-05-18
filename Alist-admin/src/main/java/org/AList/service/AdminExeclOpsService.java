package org.AList.service;

import org.AList.domain.dao.entity.StudentDefaultInfoDO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 管理员execl操作服务接口层
 */
public interface AdminExeclOpsService {
    /**
     * 保存学生学籍数据到数据库
     */
    void saveStudentDefInfo(List<StudentDefaultInfoDO> cachedDataList);

    /**
     * 解析学生学籍数据
     */
    void importStudentDefInfo(MultipartFile file) throws IOException;
}
