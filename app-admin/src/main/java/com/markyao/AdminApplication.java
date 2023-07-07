package com.markyao;


import MicroRpc.framework.beans.ProtocolTypes;
import MicroRpc.framework.context.SpringDubboApplicationContext;
import MicroRpc.framework.loadbalance.LoadBalance;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;



@EnableAspectJAutoProxy(exposeProxy = true)//暴露代理对象
@SpringBootApplication
@MapperScan("com.markyao.mapper")
public class AdminApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(AdminApplication.class, args);
        ConfigurableListableBeanFactory configurableListableBeanFactory =null;
        while (configurableListableBeanFactory==null){
            sleep(100);
            configurableListableBeanFactory=run.getBeanFactory();
        }
        SpringDubboApplicationContext context=
                new SpringDubboApplicationContext(configurableListableBeanFactory, ProtocolTypes.DUBBO, LoadBalance.RANDAM_WEIGHT);
        context.refresh();
    }

    private static void sleep(int times) {
        try {
            Thread.sleep(times);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
