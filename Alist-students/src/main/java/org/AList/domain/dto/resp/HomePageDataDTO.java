package org.AList.domain.dto.resp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomePageDataDTO {

    /**
     * 学生账号情况
     */
    private DataStatisticDTO dataStatistic;

    /**
     * 学生信息
     */
    private StuInfoDTO stuInfo;
}
