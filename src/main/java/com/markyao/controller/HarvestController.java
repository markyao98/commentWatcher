package com.markyao.controller;

import com.markyao.async.ThreadService;
import com.markyao.model.dto.RestData;
import com.markyao.service.harvest.CommentService;
import com.markyao.service.harvest.RequestURLService;
import com.markyao.service.harvest.impl.RequestURLServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ConcurrentHashMap;

import static com.markyao.service.harvest.CommentService.STOP_HARVEST_COMMENTS;

@RestController
public class HarvestController {
    @Autowired
    RequestURLService requestURLService;
    @Autowired
    CommentService commentService;
    @Autowired
    ThreadService threadService;
    @Autowired
    ConcurrentHashMap<String,Object>stateMap;
    @Autowired
    ConcurrentHashMap<String,Object>bufMap;


    @GetMapping("startHarvestUrl")
    public RestData startHarvestUrl(String url){
        if (stateMap.containsKey(RequestURLServiceImpl.STOP_HARVEST_URL)){
            stateMap.remove(RequestURLServiceImpl.STOP_HARVEST_URL);
        }
        threadService.startHarvestUrl(url,requestURLService);
        return RestData.success(null).setMsg("已通知评论收集系统开始工作~ ");
    }

    @GetMapping("stopHarvestUrl")
    public RestData stopHarvestUrl(){
        stateMap.put(RequestURLServiceImpl.STOP_HARVEST_URL,1);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return RestData.success(null).setMsg("已通知评论收集系统停止工作~ 请开始评论处理的工作");
    }

    @GetMapping("startHarvestComments")
    public RestData startHarvestComments(){
        if (stateMap.containsKey(STOP_HARVEST_COMMENTS)){
            stateMap.remove(STOP_HARVEST_COMMENTS);
        }
        threadService.deleteOldUrls();
        threadService.startHarvestAllComments(commentService);
        return RestData.success(null).setMsg("开始开始开始==已通知评论后端处理系统开始工作~~~  ===开始开始开始 ");
    }

    @GetMapping("pauseHarvestComments")
    public RestData pauseHarvestComments(){
        stateMap.put(STOP_HARVEST_COMMENTS,1);
        return RestData.success(null).setMsg("暂停暂停暂停== 已通知评论后端处理系统暂停工作!!!  ==暂停暂停暂停  ");
    }

    @GetMapping("stopHarvestComments")
    public RestData stopHarvestComments(){
        stateMap.put(STOP_HARVEST_COMMENTS,2);
        return RestData.success(null).setMsg("停止停止停止== 已通知评论后端处理系统停止工作!!!  ==停止停止停止  ");
    }

    @GetMapping("curDealingCounts")
    public RestData curDealingCounts(){
        int o = 0;
        try {
            o = (int) bufMap.get(CommentService.CUR_HARVEST_COMMENTS_CNT);
        } catch (Exception e) {}
        if (stateMap.containsKey(STOP_HARVEST_COMMENTS) && ((int)stateMap.get(STOP_HARVEST_COMMENTS)==1 || (int)stateMap.get(STOP_HARVEST_COMMENTS)==2)){
            o=0;
        }
        return RestData.success(o).setMsg("当前已收集处理【 "+o+" 】条数据");
    }
}
