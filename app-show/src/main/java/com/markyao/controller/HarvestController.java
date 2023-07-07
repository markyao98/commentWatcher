package com.markyao.controller;

import MicroRpc.framework.commons.ServiceRefrence;
import com.markyao.model.dto.RestData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import publicInterface.RpcHarvestService;

@RestController
public class HarvestController {

    @ServiceRefrence
    RpcHarvestService rpcHarvestService;


    @GetMapping("startHarvestUrl")
    public RestData startHarvestUrl(String url,String title){
        return rpcHarvestService.startHarvestUrl(url,title);
/*
        if (stateMap.containsKey(RequestURLServiceImpl.STOP_HARVEST_URL)){
            stateMap.remove(RequestURLServiceImpl.STOP_HARVEST_URL);
        }
//        requestURLService.start0(url,1,1);
        threadWorker.startForAid(url);
        return RestData.success(null).setMsg("已通知评论收集系统开始工作~ ");*/
    }

  @GetMapping("startHarvestUrlAndMonitor")
    public RestData startHarvestUrlAndMonitor(String url,String title){
        return rpcHarvestService.startHarvestUrlAndMonitor(url,title);
    /*    String aid=url;
        if (stateMap.containsKey(RequestURLServiceImpl.STOP_HARVEST_URL)){
            stateMap.remove(RequestURLServiceImpl.STOP_HARVEST_URL);
        }

        monitorLivingMap.put(aid,new Date());
//        requestURLService.start0(url,1,1);
        threadWorker.startForAid0(aid);
        return RestData.success(null).setMsg("已通知评论收集系统开始工作~ ");*/
    }


    @GetMapping("stopHarvestUrl")
    public RestData stopHarvestUrl(){
        return rpcHarvestService.stopHarvestUrl();
        /*stateMap.put(RequestURLServiceImpl.STOP_HARVEST_URL,1);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return RestData.success(null).setMsg("已通知评论收集系统停止工作~ 请开始评论处理的工作");*/
    }

    @GetMapping("startHarvestComments")
    public RestData startHarvestComments(){
        return rpcHarvestService.startHarvestComments();
       /* if (stateMap.containsKey(STOP_HARVEST_COMMENTS)){
            stateMap.remove(STOP_HARVEST_COMMENTS);
        }
//        threadService.deleteOldUrls();
        threadService.startHarvestAllComments(harvestCommentService);
        return RestData.success(null).setMsg("开始开始开始==已通知评论后端处理系统开始工作~~~  ===开始开始开始 ");*/
    }

    @GetMapping("pauseHarvestComments")
    public RestData pauseHarvestComments(){
        return rpcHarvestService.pauseHarvestComments();
        /*stateMap.put(STOP_HARVEST_COMMENTS,1);
        return RestData.success(null).setMsg("暂停暂停暂停== 已通知评论后端处理系统暂停工作!!!  ==暂停暂停暂停  ");*/
    }

    @GetMapping("stopHarvestComments")
    public RestData stopHarvestComments(){
        return rpcHarvestService.stopHarvestComments();
        /*stateMap.put(STOP_HARVEST_COMMENTS,2);
        return RestData.success(null).setMsg("停止停止停止== 已通知评论后端处理系统停止工作!!!  ==停止停止停止  ");*/
    }

    @GetMapping("curDealingCounts")
    public RestData curDealingCounts(){
        return rpcHarvestService.curDealingCounts();
        /*int o = 0;
        try {
            o = (int) bufMap.get(CUR_HARVEST_COMMENTS_CNT);
        } catch (Exception e) {}
        if (stateMap.containsKey(STOP_HARVEST_COMMENTS) && ((int)stateMap.get(STOP_HARVEST_COMMENTS)==1 || (int)stateMap.get(STOP_HARVEST_COMMENTS)==2)){
            o=0;
        }
        return RestData.success(o).setMsg("当前已收集处理【 "+o+" 】条数据");*/
    }
}
