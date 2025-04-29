package org.AList;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("org.AList.domain.dao.mapper")
public class AListStudentApplication {
    public static void main(String[] args) {
        SpringApplication.run(AListStudentApplication.class, args);
    }
}
