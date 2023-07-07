package com.markyao.rpc.provider;

import com.markyao.model.dto.RestData;
import com.markyao.service.AnalyzerService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import publicInterface.RpcAnalyzerSerivce;

import java.util.Map;

@DubboService
public class RpcAnalyzerSerivceImpl implements RpcAnalyzerSerivce {
    @Autowired
    AnalyzerService analyzerService;
    @Override
    public RestData analyzerByAid(String[] aids) {
        Map<String, Object> analyzer = analyzerService.analyzer(aids);
        return RestData.success(analyzer);
    }
}
