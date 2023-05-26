package com.markyao.service;

import com.markyao.model.pojo.IpModel;

import java.util.List;
import java.util.Map;

public interface AnalyzerService {

    String getTextByAid(String aid);

    Map<String,Object> analyzer(String... aid);

    Map<String,Object> analyzeripModelList(String aid);

    List<Map> analyzerDatetime(String aid);




}
