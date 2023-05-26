package com.markyao.service;

import com.markyao.service.harvest.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class Test2 {
    @Autowired
    AnalyzerService analyzerService;

    @Test
    void t1(){
        Map<String, Object> map = analyzerService.analyzeripModelList("7199620500274941239");
        System.out.println(map);
    }
   @Test
    void t2(){
       List<Map> maps = analyzerService.analyzerDatetime("7195361357628214589");
       System.out.println(maps);
   }
   @Autowired
    CommentService commentService;

    @Test
    void t3(){
//        commentService.harvestComments("");
    }

}
