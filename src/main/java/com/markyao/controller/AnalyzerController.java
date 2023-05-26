package com.markyao.controller;

import com.markyao.model.dto.RestData;
import com.markyao.service.AnalyzerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AnalyzerController {
    @Autowired
    AnalyzerService analyzerService;

//    @GetMapping("analyezer/{aid}")
//    public RestData analyzerByAid(@PathVariable("aid")String aid){
//        Map<String, Object> analyzer = analyzerService.analyzer(aid);
//
//        return RestData.success(analyzer);
//    }
    @GetMapping("analyezer")
    public RestData analyzerByAid(@RequestParam("aids") String[] aids){
        Map<String, Object> analyzer = analyzerService.analyzer(aids);

        return RestData.success(analyzer);
    }

//    @GetMapping("analyezer/ipSearch")
//    public RestData
}
