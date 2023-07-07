package com.markyao.rpc.impl;

import MicroRpc.framework.commons.ServiceProvider;
import com.markyao.async.ThreadService;
import com.markyao.async.ThreadWorker;
import com.markyao.model.dto.RestData;
import com.markyao.service.HarvestCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import publicInterface.RpcHarvestService;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import static com.markyao.common.States.CUR_HARVEST_COMMENTS_CNT;
import static com.markyao.common.States.STOP_HARVEST_COMMENTS;
import static com.markyao.service.impl.RequestURLServiceImpl.STOP_HARVEST_URL;

@Component
@ServiceProvider
public class RpcHarvestServiceImpl implements RpcHarvestService {
    @Autowired
    ConcurrentHashMap<String,Object> stateMap;
    @Autowired
    ConcurrentHashMap<String,Object>bufMap;
    @Autowired
    ThreadWorker threadWorker;
    @Autowired
    ConcurrentHashMap<String, Date>monitorLivingMap;
    @Autowired
    ThreadService threadService;
    @Autowired
    HarvestCommentService harvestCommentService;


    @Override
    public RestData startHarvestUrl(String url,String title) {
        if (stateMap.containsKey(STOP_HARVEST_URL)){
            stateMap.remove(STOP_HARVEST_URL);
        }
//        requestURLService.start0(url,1,1);
        threadWorker.startForAid(url,title);
        return RestData.success(null).setMsg("已通知评论收集系统开始工作~ ");
    }

    @Override
    public RestData startHarvestUrlAndMonitor(String url,String title) {
        String aid=url;
        if (stateMap.containsKey(STOP_HARVEST_URL)){
            stateMap.remove(STOP_HARVEST_URL);
        }

        monitorLivingMap.put(aid,new Date());
//        requestURLService.start0(url,1,1);
        threadWorker.startForAid0(aid,title);
        return RestData.success(null).setMsg("已通知评论收集系统开始工作~ ");
    }

    @Override
    public RestData stopHarvestUrl() {
        stateMap.put(STOP_HARVEST_URL,1);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return RestData.success(null).setMsg("已通知评论收集系统停止工作~ 请开始评论处理的工作");
    }

    @Override
    public RestData startHarvestComments() {
         if (stateMap.containsKey(STOP_HARVEST_COMMENTS)){
            stateMap.remove(STOP_HARVEST_COMMENTS);
        }
//        threadService.deleteOldUrls();
        threadService.startHarvestAllComments(harvestCommentService);
        return RestData.success(null).setMsg("开始开始开始==已通知评论后端处理系统开始工作~~~  ===开始开始开始 ");
    }

    @Override
    public RestData pauseHarvestComments() {
        stateMap.put(STOP_HARVEST_COMMENTS,1);
        return RestData.success(null).setMsg("暂停暂停暂停== 已通知评论后端处理系统暂停工作!!!  ==暂停暂停暂停  ");
    }

    @Override
    public RestData stopHarvestComments() {
        stateMap.put(STOP_HARVEST_COMMENTS,2);
        return RestData.success(null).setMsg("停止停止停止== 已通知评论后端处理系统停止工作!!!  ==停止停止停止  ");
    }

    @Override
    public RestData curDealingCounts() {
        int o = 0;
        try {
            o = (int) bufMap.get(CUR_HARVEST_COMMENTS_CNT);
        } catch (Exception e) {}
        if (stateMap.containsKey(STOP_HARVEST_COMMENTS) && ((int)stateMap.get(STOP_HARVEST_COMMENTS)==1 || (int)stateMap.get(STOP_HARVEST_COMMENTS)==2)){
            o=0;
        }
        return RestData.success(o).setMsg("当前已收集处理【 "+o+" 】条数据");
    }
}
