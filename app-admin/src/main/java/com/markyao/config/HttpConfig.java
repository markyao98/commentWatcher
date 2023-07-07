package com.markyao.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpConfig {
    @Autowired
    private ApplicationContext ctx;

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory){
        RestTemplate restTemplate = new RestTemplate(factory);

        //Interceptors 添加写的 Interceptors
//        Map<String, ClientHttpRequestInterceptor> map = ctx.getBeansOfType(ClientHttpRequestInterceptor.class);
//        List<ClientHttpRequestInterceptor> collect = map.values().stream().collect(Collectors.toList());
//        restTemplate.setInterceptors(collect);
        //BufferingClientHttpRequestFactory  此处替换为BufferingClientHttpRequestFactory
        BufferingClientHttpRequestFactory requestFactory = new BufferingClientHttpRequestFactory(factory);
        restTemplate.setRequestFactory(requestFactory);

        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(1500000);
        factory.setReadTimeout(600000);

        return factory;
    }



}
