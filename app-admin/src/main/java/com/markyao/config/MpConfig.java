package com.markyao.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MpConfig {
    //分页配置
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();

    }
}
