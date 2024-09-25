package com.pxl.zkmall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.pxl.zkmall.coupon.dao")
@EnableFeignClients
public class zkmallCouponApplication {
    public static void main(String[] args) {
        SpringApplication.run(zkmallCouponApplication.class, args);
    }
}