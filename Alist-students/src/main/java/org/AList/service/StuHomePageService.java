package org.AList.service;

import org.AList.domain.dto.resp.HomePageQueryRespDTO;

public interface StuHomePageService {
    HomePageQueryRespDTO queryHomepageInfo(String studentId);
}
