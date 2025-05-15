package org.AList.domain.dto.baseDTO;

// 1. 创建基础DTO接口
public interface BoardBaseDTO {
    Integer getBoardId();
    String getTitle();
    String getCategory();
    String getContent();
    Integer getStatus();
    String getPriority();
    String getCoverImage();
}