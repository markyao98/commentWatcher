package com.markyao.controller;

import MicroRpc.framework.commons.ServiceRefrence;
import com.markyao.model.dto.RestData;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import publicInterface.RpcMonitorService;


@RestController
public class MonitorController {
    @ServiceRefrence
    RpcMonitorService rpcMonitorService;

    @GetMapping("monitorStart/digCount")
    public RestData digCount(@RequestParam String[] aids,
                             @RequestParam(value = "timeStand",defaultValue = "30") Integer timeStand){
        return rpcMonitorService.digCount(aids,timeStand);
        /*try {
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
        }*/
    }
    @GetMapping("monitorStop/digCount")
    public RestData monitorStop(@RequestParam String[] aids){
        return rpcMonitorService.monitorStop(aids);
       /* for (String aid : aids) {
            monitorLivingMap.remove(aid);
        }
        if (stateMap.containsKey(MONITORING)){
            HashSet monitoringSet = (HashSet) stateMap.getOrDefault(MONITORING, new HashSet<String>());
            for (String aid : aids) {
                monitoringSet.remove(aid);
            }
            stateMap.put(MONITORING,monitoringSet);
        }
        return RestData.success(null).setMsg("已经停止监控~~");*/
    }

    @GetMapping("monitor/isGoing")
    public RestData monitorIsGoing(@RequestParam String aid){
        return rpcMonitorService.monitorIsGoing(aid);
        /*boolean b = monitorLivingMap.containsKey(aid);
        return RestData.success(b?1:0);*/
    }

    @GetMapping("/moditor/show")
    public RestData monitorShow(@RequestParam String did){
        return rpcMonitorService.monitorShow(did);
      /*  MonitorShowVo monitorShowVos=monitorShowService.showForDid(did);
        return RestData.success(monitorShowVos);*/
    }

    @GetMapping("monitor/isGoingList")
    public RestData monitorIsGoingList(){
        return rpcMonitorService.monitorIsGoingList();
       /* List<MonitorVo> monitorVos=monitorDigCountService.getList(monitorLivingMap);
        return RestData.success(monitorVos);*/
    }
    @GetMapping("monitor/list")
    public RestData monitorList(){
        return rpcMonitorService.monitorList();
       /* List<MonitorVo> monitorVos=monitorDigCountService.getList();
        return RestData.success(monitorVos);*/
    }


    @GetMapping("monitor/BoomList")
    public RestData monitorBoomList(){
        return rpcMonitorService.monitorBoomList();
        /*List<MonitorPowerVo> monitorPowerVoList=monitorDigCountService.getBoomList(monitorPowerMap);
        return RestData.success(monitorPowerVoList);*/
    }

}
