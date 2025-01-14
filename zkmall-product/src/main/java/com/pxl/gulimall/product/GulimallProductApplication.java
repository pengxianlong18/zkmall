package com.pxl.zkmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@MapperScan("com.pxl.zkmall.product.dao") // 扫描Mapper接口)
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.pxl.zkmall.product.feign")
@EnableRedisHttpSession
public class zkmallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run( zkmallProductApplication .class, args);
    }
}