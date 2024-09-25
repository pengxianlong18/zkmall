package com.pxl.zkmall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * 1、锁的自动续期，如果业务超长，运行期间自动给锁续上新的30s，默认加锁的时间30s
 * 2、加锁的业务只要运行完成，不会给当前锁续期，即使不手动解锁，锁默认30s后自动删除
 */

@Configuration
public class RedissonConfig {
    @Bean(destroyMethod="shutdown")
    public  RedissonClient redisson() throws IOException {
        // 1.创建配置
        // 2.设置redis地址
        Config config = new Config();

        //  Redis url should start with redis:// or rediss:// (for SSL connection)
        config.useSingleServer().setAddress("redis://192.168.6.128:6379");
        // 3、根据config创建RedissonClient实例
        return Redisson.create(config);
    }
}
