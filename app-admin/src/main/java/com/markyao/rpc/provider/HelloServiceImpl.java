package com.markyao.rpc.provider;

import org.apache.dubbo.config.annotation.DubboService;
import publicInterface.HelloService;
//@DubboService
public class HelloServiceImpl implements HelloService {
    @Override
    public String say(String msg) {
        return "hello from admin ! "+msg;
    }
}
