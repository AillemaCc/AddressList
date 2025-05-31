package org.AList.service;

import org.AList.domain.dto.resp.HomePageDataDTO;

public interface StuHomePageService {
    HomePageDataDTO queryHomepageInfo(String studentId);
}
