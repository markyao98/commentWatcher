package com.markyao;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)//暴露代理对象
@EnableDubbo
@MapperScan("com.markyao.mapper")
@SpringBootApplication
public class HarvestApplication {
    public static void main(String[] args) {
        SpringApplication.run(HarvestApplication.class,args);
    }
}
