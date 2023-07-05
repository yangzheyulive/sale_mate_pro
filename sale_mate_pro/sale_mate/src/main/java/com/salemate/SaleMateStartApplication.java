package com.salemate;

import com.salemate.common.config.WxProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.salemate.mapper")
@EnableScheduling
@EnableConfigurationProperties(WxProperties.class)
public class SaleMateStartApplication {
    public static void main(String[] args) {
        SpringApplication.run(SaleMateStartApplication.class, args);
    }
}
