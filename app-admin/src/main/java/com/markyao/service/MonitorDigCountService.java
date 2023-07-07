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

    List<MonitorVo> getList(Map<String, Date> monitorLivingMap);
    List<MonitorVo> getList();


}
