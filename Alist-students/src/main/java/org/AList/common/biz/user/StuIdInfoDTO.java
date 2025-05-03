package org.AList.common.biz.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学生用户上下文数据传输对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StuIdInfoDTO {
    /**
     * 学生学号
     */
    private String studentId;
}
