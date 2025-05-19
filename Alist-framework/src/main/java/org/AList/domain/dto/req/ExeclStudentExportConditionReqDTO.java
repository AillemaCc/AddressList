package org.AList.domain.dto.req;

import lombok.Data;

@Data
public class ExeclStudentExportConditionReqDTO {
    private String majorNum;   // 专业代码
    private String classNum;   // 班级
    private String enrollmentYear; // 入学年份
    private String graduationYear; // 毕业年份
}