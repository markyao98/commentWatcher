package com.markyao.service;

import com.markyao.model.vo.MonitorPowerVo;
import com.markyao.model.vo.MonitorVo;
import org.apache.tomcat.util.json.ParseException;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface MonitorDigCountService {
    void monitorForDigCount(String aid,int timeStand) throws IOException, ParseException;
    void monitorForDigCount2(String aid,int timeStand) throws IOException, ParseException;
    void monitorForDigCount3(String aid,int cur,int timeStand) throws IOException, ParseException;

    List<MonitorVo> getList(Map<String, Date> monitorLivingMap);
    List<MonitorVo> getList();

    List<MonitorPowerVo> getBoomList(ConcurrentHashMap<String, Object> monitorPowerMap);
}
