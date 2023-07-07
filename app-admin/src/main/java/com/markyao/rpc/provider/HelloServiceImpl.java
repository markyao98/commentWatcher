package com.markyao.rpc.provider;

import MicroRpc.framework.commons.ServiceProvider;

import org.springframework.stereotype.Component;
import publicInterface.HelloService;
@Component
@ServiceProvider
public class HelloServiceImpl implements HelloService {
    @Override
    public String say(String msg) {
        return "hello ! "+msg;
    }
}
