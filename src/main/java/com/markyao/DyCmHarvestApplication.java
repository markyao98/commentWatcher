package com.markyao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)//暴露代理对象
@MapperScan("com.markyao.mapper")
public class DyCmHarvestApplication {
    public static void main(String[] args) {
        SpringApplication.run(DyCmHarvestApplication.class,args);
    }
}
