package org.AList.service.jwt;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import java.security.SecureRandom;
import java.util.*;

@Component  
@Slf4j  
public class JwtKeyRotationService {  
      
    @Value("${nacos.config.server-addr:127.0.0.1:8848}")  
    private String serverAddr;  
      
    @Value("${nacos.config.namespace:}")  
    private String namespace;  
      
    @Value("${nacos.config.group:DEFAULT_GROUP}")  
    private String group;  
      
    private ConfigService configService;  
    private SecureRandom secureRandom = new SecureRandom();  
      
    @PostConstruct  
    public void init() throws Exception {  
        Properties properties = new Properties();  
        properties.put("serverAddr", serverAddr);  
        properties.put("namespace", namespace);  
        configService = NacosFactory.createConfigService(properties);  
    }  
      
    /**  
     * 定期轮换JWT密钥 - 每7天执行一次  
     */  
    @Scheduled(cron = "0 0 2 */7 * ?") // 每7天凌晨2点执行  
    public void rotateJwtSecret() {  
        try {  
            log.info("开始执行JWT密钥轮换...");  
              
            // 生成新的JWT密钥  
            String newSecret = generateNewSecret();  
              
            // 更新Nacos配置  
            updateNacosConfig(newSecret);  
              
            log.info("JWT密钥轮换完成，新密钥长度: {}", newSecret.length());  
              
        } catch (Exception e) {  
            log.error("JWT密钥轮换失败", e);  
        }  
    }  
      
    /**  
     * 手动触发密钥轮换  
     */  
    public void manualRotateSecret() {  
        rotateJwtSecret();  
    }  
      
    /**  
     * 生成新的JWT密钥  
     */  
    private String generateNewSecret() {  
        // 生成256位(32字节)的随机密钥  
        byte[] keyBytes = new byte[32];  
        secureRandom.nextBytes(keyBytes);  
        return Base64.getEncoder().encodeToString(keyBytes);  
    }

    /**
     * 更新Nacos配置中的JWT密钥，并管理历史密钥
     */
    private void updateNacosConfig(String newSecret) throws Exception {
        String currentConfig = configService.getConfig("alist-common.yml", group, 5000);

        if (currentConfig != null) {
            Yaml yaml = new Yaml();
            Map<String, Object> configMap = yaml.load(currentConfig);

            Map<String, Object> jwtConfig = (Map<String, Object>) configMap.get("jwt");
            if (jwtConfig != null) {
                // 获取当前密钥
                String currentSecret = (String) jwtConfig.get("secret");

                // 获取历史密钥列表
                List<String> historicalSecrets = getHistoricalSecretsList(jwtConfig);
                if (historicalSecrets == null) {
                    historicalSecrets = new ArrayList<>();
                }

                // 将当前密钥添加到历史密钥列表
                if (currentSecret != null && !currentSecret.equals(newSecret)) {
                    historicalSecrets.add(0, currentSecret); // 添加到列表开头
                }

                // 限制历史密钥数量
                Integer maxHistoricalKeys = (Integer) jwtConfig.get("maxHistoricalKeys");
                if (maxHistoricalKeys == null) {
                    maxHistoricalKeys = 3;
                }

                while (historicalSecrets.size() > maxHistoricalKeys) {
                    String removedKey = historicalSecrets.remove(historicalSecrets.size() - 1);
                    log.info("移除过期历史密钥: {}...", removedKey.substring(0, Math.min(10, removedKey.length())));
                }

                // 更新配置
                jwtConfig.put("secret", newSecret);
                jwtConfig.put("historicalSecrets", historicalSecrets);

                String updatedConfig = yaml.dump(configMap);
                boolean result = configService.publishConfig("alist-common.yml", group, updatedConfig);

                if (result) {
                    log.info("JWT密钥已成功更新到Nacos配置中心，历史密钥数量: {}", historicalSecrets.size());
                } else {
                    throw new RuntimeException("更新Nacos配置失败");
                }
            }
        }
    }

    // 新增辅助方法
    private List<String> getHistoricalSecretsList(Map<String, Object> jwtConfig) {
        Object historicalSecretsObj = jwtConfig.get("historicalSecrets");
        List<String> historicalSecrets = new ArrayList<>();

        if (historicalSecretsObj instanceof List) {
            List<?> rawList = (List<?>) historicalSecretsObj;
            for (Object item : rawList) {
                if (item instanceof String) {
                    String secret = (String) item;
                    // 清理可能的方括号
                    secret = secret.trim();
                    if (secret.startsWith("[")) {
                        secret = secret.substring(1);
                    }
                    if (secret.endsWith("]")) {
                        secret = secret.substring(0, secret.length() - 1);
                    }
                    // 验证是否为有效的base64字符串
                    if (isValidBase64(secret)) {
                        historicalSecrets.add(secret);
                    }
                }
            }
        }

        return historicalSecrets;
    }

    // 添加base64验证方法
    private boolean isValidBase64(String str) {
        try {
            Base64.getDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            log.warn("无效的base64密钥: {}", str);
            return false;
        }
    }
}