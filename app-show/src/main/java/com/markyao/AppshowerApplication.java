package com.markyao;


import MicroRpc.framework.beans.ProtocolTypes;
import MicroRpc.framework.context.SpringDubboApplicationContext;
import MicroRpc.framework.loadbalance.LoadBalance;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
//@EnableDubbo
public class AppshowerApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(AppshowerApplication.class, args);
        ConfigurableListableBeanFactory configurableListableBeanFactory = run.getBeanFactory();
        SpringDubboApplicationContext context=
                new SpringDubboApplicationContext(configurableListableBeanFactory, ProtocolTypes.DUBBO, LoadBalance.RANDAM_WEIGHT);
        context.refresh();
    }

}
