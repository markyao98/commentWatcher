package com.markyao.controller;

import MicroRpc.framework.commons.ServiceRefrence;
import com.markyao.model.dto.RestData;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import publicInterface.HelloService;
import publicInterface.RpcIndexService;

/**
 * 展示信息，例如监控列表，分组列表等
 */
@RestController
public class IndexController {

    @ServiceRefrence
    RpcIndexService rpcIndexService;

    /**
     * 返回分组的aid，同时要给与视频的标题信息
     * @return
     */
    @GetMapping("index")
    public RestData indexData(){
        return rpcIndexService.getVideos();
    }


    /**
     * 返回分组的aid，同时要给与视频的标题信息
     * @return
     */
    @GetMapping("index/monitors")
    public RestData indexMonitorsData(){
        return rpcIndexService.indexMonitorsData();
    }

    /**
     * 返回分组的aid，同时要给与视频的标题信息
     * @return
     */
    @GetMapping("index/groups")
    public RestData indexGroups(){
        return rpcIndexService.indexGroups();

    }


    @ServiceRefrence
    HelloService helloService;
    @GetMapping("test1")
    public RestData test1(String msg){
        return RestData.success(helloService.say(msg));
    }
}
