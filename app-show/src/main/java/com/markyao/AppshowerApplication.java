package com.markyao;


import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class AppshowerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppshowerApplication.class,args);
    }

}
