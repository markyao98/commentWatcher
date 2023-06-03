package com.markyao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class LocalBufConfig {


    @Bean("stateMap")
    public ConcurrentHashMap<String,Object>stateMap(){
        return new ConcurrentHashMap<>(16);
    }
    @Bean("bufMap")
    public ConcurrentHashMap<String,Object>bufMap(){
        return new ConcurrentHashMap<>(16);
    }

    @Bean("ALLInState")
    public AtomicInteger ALLInState(){
        return new AtomicInteger(0);
    }

    @Bean("monitorLivingMap")
    public ConcurrentHashMap<String, Date>monitorLivingMap(){
        return new ConcurrentHashMap<>(16);
    }
    @Bean("monitorPowerMap")
    public ConcurrentHashMap<String, Object>monitorPowerMap(){
        return new ConcurrentHashMap<>(16);
    }

}
