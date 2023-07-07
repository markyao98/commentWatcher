package com.markyao;

import MicroRpc.framework.beans.ProtocolTypes;
import MicroRpc.framework.context.AbstractApplicationContext;
import MicroRpc.framework.context.DefaultDubboApplicationContext;
import MicroRpc.framework.context.SpringDubboApplicationContext;
import MicroRpc.framework.loadbalance.LoadBalance;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.annotation.PostConstruct;

@EnableAspectJAutoProxy(exposeProxy = true)//暴露代理对象
@MapperScan("com.markyao.mapper")
@SpringBootApplication
public class HarvestApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(HarvestApplication.class, args);
        ConfigurableListableBeanFactory configurableListableBeanFactory = run.getBeanFactory();
        SpringDubboApplicationContext context=
                new SpringDubboApplicationContext(configurableListableBeanFactory, ProtocolTypes.DUBBO, LoadBalance.RANDAM_WEIGHT);
        context.refresh();
    }


}
