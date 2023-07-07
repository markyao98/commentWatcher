package com.markyao.rpc.impl;

import MicroRpc.framework.commons.ServiceProvider;
import org.springframework.stereotype.Component;
import publicInterface.HelloService;

@Component
@ServiceProvider
public class HelloServiceImpl implements HelloService {
    @Override
    public String say(String msg) {
        return "from: app-havest hello!! "+msg;
    }
}
