package com.markyao.rpc.impl;

import com.markyao.async.ThreadService;
import com.markyao.async.ThreadWorker;
import com.markyao.model.dto.RestData;
import com.markyao.model.vo.MonitorPowerVo;
import com.markyao.model.vo.MonitorShowVo;
import com.markyao.model.vo.MonitorVo;
import com.markyao.service.MonitorDigCountService;
import com.markyao.service.impl.MonitorShowService;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import publicInterface.RpcMonitorService;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.markyao.common.States.MONITORING;

@DubboService
public class RpcMonitorServiceImpl implements RpcMonitorService {
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
    MonitorDigCountService monitorDigCountService;
    @Autowired
    MonitorShowService monitorShowService;
    @Autowired
    ConcurrentHashMap<String, Object> monitorPowerMap;

    @Override
    public RestData digCount(String[] aids, Integer timeStand) {
        try {
            for (String aid : aids) {
                monitorLivingMap.put(aid,new Date());
                HashSet monitoringSet=null;
                if (stateMap.containsKey(MONITORING)){
                    monitoringSet = (HashSet) stateMap.getOrDefault(MONITORING, new HashSet<String>());
                }else {
                    monitoringSet=new HashSet();
                }
                monitoringSet.add(aid);
                stateMap.put(MONITORING,monitoringSet);
                threadService.startMonitor(aid,timeStand,monitorDigCountService);
            }
            return RestData.success(null).setMsg("已经开始监控~~");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return RestData.fails("开启监控失败！！！");

        }
    }

    @Override
    public RestData monitorStop(String[] aids) {
         for (String aid : aids) {
            monitorLivingMap.remove(aid);
        }
        if (stateMap.containsKey(MONITORING)){
            HashSet monitoringSet = (HashSet) stateMap.getOrDefault(MONITORING, new HashSet<String>());
            for (String aid : aids) {
                monitoringSet.remove(aid);
            }
            stateMap.put(MONITORING,monitoringSet);
        }
        return RestData.success(null).setMsg("已经停止监控~~");
    }

    @Override
    public RestData monitorIsGoing(String aid) {
        boolean b = monitorLivingMap.containsKey(aid);
        return RestData.success(b?1:0);
    }


    @Override
    public RestData monitorShow(String did) {
        MonitorShowVo monitorShowVos=monitorShowService.showForDid(did);
        return RestData.success(monitorShowVos);
    }

    @Override
    public RestData monitorIsGoingList() {
        List<MonitorVo> monitorVos=monitorDigCountService.getList(monitorLivingMap);
        return RestData.success(monitorVos);
    }

    @Override
    public RestData monitorList() {
        List<MonitorVo> monitorVos=monitorDigCountService.getList();
        return RestData.success(monitorVos);
    }

    @Override
    public RestData monitorBoomList() {
        List<MonitorPowerVo> monitorPowerVoList=monitorDigCountService.getBoomList(monitorPowerMap);
        return RestData.success(monitorPowerVoList);
    }
}
