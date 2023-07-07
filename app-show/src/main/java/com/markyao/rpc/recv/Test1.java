//package com.markyao.rpc.recv;
//
//import MicroRpc.framework.beans.MicroRpc.framework.beans.ProtocolTypes;
//import MicroRpc.framework.beans.MicroRpc.framework.context.AbstractApplicationContext;
//import MicroRpc.framework.beans.MicroRpc.framework.context.DefaultDubboApplicationContext;
//import MicroRpc.framework.beans.MicroRpc.framework.loadbalance.LoadBalance;
//import publicInterface.HelloService;
//
//public class Test1 {
//    public static void main(String[] args) {
//        AbstractApplicationContext context=
//                new DefaultDubboApplicationContext(ProtocolTypes.DUBBO, LoadBalance.RANDAM_WEIGHT);
//        try {
//            context.refresh();
//            System.out.println("刷新完毕");
//            HelloService helloService = context.getProtocol().getService(HelloService.class);
//            for (int i = 0; i < 20; i++) {
//                System.out.println("===============================");
//                System.out.println(helloService.say("markyao!!!"));
//                System.out.println("===============================");
//                Thread.sleep(1000);
//            }
//        } catch (ClassNotFoundException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//}
