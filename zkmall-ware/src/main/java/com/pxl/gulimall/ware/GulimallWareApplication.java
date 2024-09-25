package com.pxl.zkmall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.pxl.zkmall.ware.dao")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.pxl.zkmall.ware.feign")
@EnableRabbit
public class zkmallWareApplication {
    public static void main(String[] args) {
        SpringApplication.run(zkmallWareApplication.class, args);
    }
}