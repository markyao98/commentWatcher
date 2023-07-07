package com.markyao.rpc.recv;

import MicroRpc.framework.commons.ServiceRefrence;
import MicroRpc.framework.context.SpringDubboApplicationContext;
import MicroRpc.framework.tools.IO.AnnotationUtils;
import com.markyao.model.dto.RestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import publicInterface.HelloService;
import publicInterface.RpcIndexService;
import publicInterface.test.TestRpcIndexService;

/**
 * 展示信息，例如监控列表，分组列表等
 */
@RestController
@RequestMapping("test")
public class TestIndexController {


    @ServiceRefrence
    private TestRpcIndexService testRpcIndexService;
    /**
     * 返回分组的aid，同时要给与视频的标题信息
     * @return
     */
    @GetMapping("index")
    public RestData indexData(){

        return testRpcIndexService.getVideos();
    }


    /**
     * 返回分组的aid，同时要给与视频的标题信息
     * @return
     */
    @GetMapping("index/monitors")
    public RestData indexMonitorsData(){

        return testRpcIndexService.indexMonitorsData();
    }

    /**
     * 返回分组的aid，同时要给与视频的标题信息
     * @return
     */
    @GetMapping("index/groups")
    public RestData indexGroups(){

        return testRpcIndexService.indexGroups();
    }


    @ServiceRefrence
    HelloService helloService;
    @GetMapping("test1")
    public RestData test1(String msg){
        return RestData.success(helloService.say(msg));
    }
}
