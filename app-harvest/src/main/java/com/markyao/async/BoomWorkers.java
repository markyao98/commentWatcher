package com.markyao.async;

import com.markyao.common.States;
import com.markyao.service.core.HarvestCommentWorker;
import com.markyao.utils.MonitorPowerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 专门处理爆炸评论
 */
@Slf4j
@Component
@Lazy
public class BoomWorkers {
    @Autowired
    HarvestCommentWorker harvestCommentWorker;
    @Autowired
    ConcurrentHashMap<String, Object> monitorPowerMap;
    private volatile Set<String>activitySet=new HashSet<>();
    @Async("boomTask")
    public void boomCommentMonitor(int cur,String aid,String did){
        while (monitorPowerMap.containsKey(States.POWER_MONITORING)){
            if (helpCheck(cur,aid)){
                try {
                    log.info("开启强力监控~~~");
                    harvestCommentWorker.start00(aid,cur);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
            }else {
                log.info("当前aid :{}  cur: {} 已被监控",aid,cur);
                break;
            }
//            else {
//                Set set = (Set) monitorPowerMap.get(States.POWER_MONITORING);
//                set.remove(aid+"::"+cur+"::"+did);
//                if (set.size()==0){
//                    log.info("强力监控总开关已关闭~");
//                    return;
//                }
//                log.info("强力监控已关闭: {}::{}",aid,cur);
//            }
        }

    }

    /**
     * 检查监控map中是否含有cur 和 aid
     * @param cur
     * @param aid
     * @return
     */
    private synchronized boolean helpCheck(int cur, String aid) {
        Set set = (Set) monitorPowerMap.get(States.POWER_MONITORING);

        if (set!=null){
            for (Object k : set) {
                if (MonitorPowerUtils.getAid(k.toString()).equals(aid) && MonitorPowerUtils.getCur(k.toString()).equals(cur+"")) {
                    if (activitySet.contains(aid+"::"+cur)){
                        return false;
                    }
                    activitySet.add(aid+"::"+cur);
                    return true;
                }
            }
        }
        return false;
    }
}
