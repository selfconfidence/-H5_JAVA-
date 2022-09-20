package com.manyun.common.redis.configure;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfiguration {

    public static Logger logger = LoggerFactory.getLogger(RedissonConfiguration.class);

    //@Value("${redis.addr}")
    private String addr = "127.0.0.1:6379";

    @Bean
    public RedissonClient redisson() {
        Config config = new Config();
        logger.info("加载redisson配置");
        config.useSingleServer()
                .setAddress(String.format("%s%s", "redis://", addr))
                .setConnectionPoolSize(64)              // 连接池大小
                .setConnectionMinimumIdleSize(8)        // 保持最小连接数
                .setConnectTimeout(1500)                // 建立连接超时时间
                .setTimeout(2000)                       // 执行命令的超时时间, 从命令发送成功时开始计时
                .setRetryAttempts(2)                    // 命令执行失败重试次数
                .setRetryInterval(1000);                // 命令重试发送时间间隔
        logger.info("创建redissonClient");
        return Redisson.create(config);
    }
}
