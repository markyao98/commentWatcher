package com.markyao.controller;

import com.markyao.async.ThreadService;
import com.markyao.model.dto.RestData;
import com.markyao.model.vo.MonitorPowerVo;
import com.markyao.model.vo.MonitorShowVo;
import com.markyao.model.vo.MonitorVo;
import com.markyao.service.MonitorDigCountService;
import com.markyao.service.impl.MonitorShowService;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.markyao.common.States.MONITORING;

@RestController
public class MonitorController {
    @Autowired
    MonitorDigCountService monitorDigCountService;
    @Autowired
    ThreadService threadService;
    @Autowired
    ConcurrentHashMap<String,Object> stateMap;
    @Autowired
    ConcurrentHashMap<String,Date>monitorLivingMap;
    @Autowired
    MonitorShowService monitorShowService;
    @Autowired
    ConcurrentHashMap<String, Object> monitorPowerMap;
    @GetMapping("monitorStart/digCount")
    public RestData digCount(@RequestParam String[] aids,@RequestParam(value = "timeStand",defaultValue = "30") Integer timeStand){
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
        } catch (IOException |ParseException e) {
            e.printStackTrace();
            return RestData.fails("开启监控失败！！！");
        }
    }
    @GetMapping("monitorStop/digCount")
    public RestData monitorStop(@RequestParam String[] aids){
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

    @GetMapping("monitor/isGoing")
    public RestData monitorIsGoing(@RequestParam String aid){
        boolean b = monitorLivingMap.containsKey(aid);
        return RestData.success(b?1:0);
    }

    @GetMapping("/moditor/show")
    public RestData monitorShow(@RequestParam String did){
        MonitorShowVo monitorShowVos=monitorShowService.showForDid(did);
        return RestData.success(monitorShowVos);
    }

    @GetMapping("monitor/isGoingList")
    public RestData monitorIsGoingList(){
        List<MonitorVo> monitorVos=monitorDigCountService.getList(monitorLivingMap);
        return RestData.success(monitorVos);
    }
    @GetMapping("monitor/list")
    public RestData monitorList(){
        List<MonitorVo> monitorVos=monitorDigCountService.getList();
        return RestData.success(monitorVos);
    }


    @GetMapping("monitor/BoomList")
    public RestData monitorBoomList(){
        List<MonitorPowerVo> monitorPowerVoList=monitorDigCountService.getBoomList(monitorPowerMap);
        return RestData.success(monitorPowerVoList);
    }

}
