package com.markyao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

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



}
