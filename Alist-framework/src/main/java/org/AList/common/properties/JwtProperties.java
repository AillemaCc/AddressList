package org.AList.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component  
@ConfigurationProperties(prefix = "jwt")  
@Data  
public class JwtProperties {  
    private String secret;  
    private Long expiration;
    private List<String> historicalSecrets = new ArrayList<>(); // 历史密钥列表
    private Integer maxHistoricalKeys = 3; // 最大保留历史密钥数量
    private Long keyRotationGracePeriod = 24 * 60 * 60 * 1000L; // 密钥轮换宽限期（24小时）
}  
  
