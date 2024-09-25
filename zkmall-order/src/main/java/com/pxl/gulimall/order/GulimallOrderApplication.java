package com.pxl.zkmall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


/**
 * rabbitmq 自动配置了RabbitMessagingTemplate AmqpAdmin RabbitConnectionFactoryCreator RabbitTemplate
 * @ConfigurationProperties prefix = "spring.rabbitmq"
 *
 *
 * 本地事务失效问题：
 *      同一个对象内事务方法互调默认失效，原因 绕过了代理对象，事务使用代理对象来控制的
 *      解决：使用代理对象来调用事务方法
 *        1）、引入 aop-starter 的 aspectj 依赖
 *        2）、开启aspectj动态代理功能  @EnableAspectJAutoProxy(proxyTargetClass = true) 对外暴露代理对象
 *        3）、
 */

@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootApplication
@MapperScan("com.pxl.zkmall.order.dao")
@EnableDiscoveryClient
@EnableFeignClients
@EnableRabbit
@EnableRedisHttpSession
public class zkmallOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(zkmallOrderApplication.class,args);
    }
}