package org.AList.domain.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuerySomeoneRespDTO {
    private String studentId;
    private String name;
    private String academyName;
    private String majorName;
    private String className;
    private String enrollmentYear;
    private String graduationYear;
}
