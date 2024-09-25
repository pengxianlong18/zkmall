package com.pxl.zkmall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


/**
 * 远程调用微服务
 * 1、引入open-feign
 * 2、编写接口，告诉springCloud这个接口需要调用远程服务
 *      1声明接口的每一个方法都是调用哪个远程服务的哪个请求
 *      2在接口里写上@FeignClient(value = "zkmall-coupon")
 */

@SpringBootApplication
@MapperScan("com.pxl.zkmall.member.dao")
@EnableDiscoveryClient
@EnableFeignClients("com.pxl.zkmall.member.feign")
@EnableRedisHttpSession
public class zkmallMemberApplication  {
    public static void main(String[] args) {
        SpringApplication.run(zkmallMemberApplication.class, args);
    }
}