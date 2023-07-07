package com.markyao.test;

import MicroRpc.framework.beans.ProtocolTypes;
import MicroRpc.framework.context.AbstractApplicationContext;
import MicroRpc.framework.context.DefaultDubboApplicationContext;
import MicroRpc.framework.loadbalance.LoadBalance;

public class Provider1test {
    public static void main(String[] args) {
        AbstractApplicationContext context
                =new DefaultDubboApplicationContext(ProtocolTypes.DUBBO, LoadBalance.CONSISTENT_HASH); //第一个参数为协议，第二个参数为负载均衡策略
        try {
            context.refresh();
            System.out.println("刷新完毕 可以干点其他事");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}