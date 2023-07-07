package com.markyao.controller;

import com.markyao.model.dto.RestData;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import publicInterface.RpcAnalyzerSerivce;

import java.util.Map;

@RestController
public class AnalyzerController {
    public static void main(String[] args) {
        System.out.println(AnalyzerController.class.getClassLoader().getResource("static").getPath()+"/"+"imgs/");
    }
     @DubboReference(timeout = 5 * 60 * 1000)
     RpcAnalyzerSerivce rpcAnalyzerSerivcel;
//    @GetMapping("analyezer/{aid}")
//    public RestData analyzerByAid(@PathVariable("aid")String aid){
//        Map<String, Object> analyzer = analyzerService.analyzer(aid);
//
//        return RestData.success(analyzer);
//    }
    @GetMapping("analyezer")
    public RestData analyzerByAid(@RequestParam("aids") String[] aids){
        return rpcAnalyzerSerivcel.analyzerByAid(aids);

    }

//    @GetMapping("analyezer/ipSearch")
//    public RestData
}
