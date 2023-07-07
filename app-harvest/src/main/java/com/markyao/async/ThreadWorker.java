package com.markyao.async;

import com.markyao.service.MonitorDigCountService;
import com.markyao.service.core.HarvestCommentWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class ThreadWorker {

    @Autowired
    HarvestCommentWorker harvestCommentWorker;
    @Autowired
    MonitorDigCountService monitorDigCountService;
    @Async("newTaskExecutor")
    public void startForAid(String aid,String title){
        log.info("开始异步采集工作~~");
        try {
            harvestCommentWorker.start0(aid,title);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
    @Async("newTaskExecutor")
    public void startForAid0(String aid,String title){
        log.info("开始异步采集工作~~");
        try {
            harvestCommentWorker.start0(aid,title);
            log.info("现在为 {} 开启监控~",aid);
            monitorDigCountService.monitorForDigCount2(aid,30);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

}
