package com.markyao.rpc.provider;

import MicroRpc.framework.commons.ServiceProvider;
import com.markyao.model.dto.RestData;
import com.markyao.service.AnalyzerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import publicInterface.RpcAnalyzerSerivce;

import java.util.Map;
@Component
@ServiceProvider
public class RpcAnalyzerSerivceImpl implements RpcAnalyzerSerivce {
    @Autowired
    AnalyzerService analyzerService;
    @Override
    public RestData analyzerByAid(String[] aids) {
        Map<String, Object> analyzer = analyzerService.analyzer(aids);
        return RestData.success(analyzer);
    }
}
