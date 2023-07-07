//package com.markyao.rpc.recv;
//
//import MicroRpc.framework.beans.MicroRpc.framework.beans.ProtocolTypes;
//import MicroRpc.framework.beans.MicroRpc.framework.context.AbstractApplicationContext;
//import MicroRpc.framework.beans.MicroRpc.framework.context.DefaultDubboApplicationContext;
//import MicroRpc.framework.beans.MicroRpc.framework.loadbalance.LoadBalance;
//import com.markyao.model.dto.RestData;
//import publicInterface.test.TestRpcIndexService;
//import publicInterface.test.TestService;
//
//public class Test3 {
//
//    public static void main(String[] args) {
//        AbstractApplicationContext context=
//                new DefaultDubboApplicationContext(ProtocolTypes.DUBBO, LoadBalance.RANDAM_WEIGHT);
//        try {
//            context.refresh();
//            System.out.println("刷新完毕");
//            TestRpcIndexService testRpcIndexService = context.getProtocol().getService(TestRpcIndexService.class);
//            RestData restData = testRpcIndexService.indexGroups();
//            System.out.println(restData);
//        } catch (ClassNotFoundException  e) {
//            e.printStackTrace();
//        }
//    }
//}
