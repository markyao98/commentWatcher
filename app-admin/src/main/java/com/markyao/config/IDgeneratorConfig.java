package com.markyao.config;

import com.markyao.utils.SnowflakeIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description 雪花ID生成器
 * @Author markyao
 * @Date  2023/5/16
 */
@Configuration
public class IDgeneratorConfig {

    @Bean
    public SnowflakeIdGenerator snowflakeIdGenerator(){
        SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(1, 1);
        return snowflakeIdGenerator;
    }
}
