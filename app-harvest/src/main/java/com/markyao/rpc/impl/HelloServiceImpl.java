package com.markyao.rpc.impl;

import org.apache.dubbo.config.annotation.DubboService;
import publicInterface.HelloService;
@DubboService
public class HelloServiceImpl implements HelloService {
    @Override
    public String say(String msg) {
        return "hello!! "+msg;
    }
}
