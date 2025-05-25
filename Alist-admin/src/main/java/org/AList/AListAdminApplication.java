package org.AList;

import org.AList.config.NacosConfigLoader;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("org.AList.domain.dao.mapper")
public class AListAdminApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(AListAdminApplication.class);
        app.addInitializers(new NacosConfigLoader());
        app.run(args);
    }
}
