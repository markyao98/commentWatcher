package com.markyao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.markyao.common.States;
import com.markyao.mapper.CommentDetailsMapper;
import com.markyao.mapper.HarvestCommentUrlMapper;
import com.markyao.mapper.MonitorCommentDiggMapper;
import com.markyao.mapper.VideoMonitoredMapper;
import com.markyao.model.pojo.*;
import com.markyao.model.vo.CommentDetailsVo;
import com.markyao.model.vo.MonitorPowerVo;
import com.markyao.model.vo.MonitorVo;
import com.markyao.model.vo.VideoInfoVo;
import com.markyao.service.MonitorDigCountService;
import com.markyao.service.VideoInfoService;
import com.markyao.utils.DateFormatUtils;
import com.markyao.utils.MonitorPowerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.markyao.common.States.MONITORING;
import static com.markyao.utils.DateFormatUtils.getFormat;
import static com.markyao.utils.FillUtils.fillCommentDetails;
import static com.markyao.utils.FillUtils.fillCommentUser;

@Service
@Slf4j
public class MonitorDigCountServiceImpl  extends ServiceImpl<MonitorCommentDiggMapper, MonitorCommentDigg> implements MonitorDigCountService {


    @Autowired
    VideoInfoService videoInfoService;
    @Override
    public List<MonitorVo> getList(Map<String, Date> monitorLivingMap) {
        List<MonitorVo>monitorVos=new ArrayList<>(monitorLivingMap.size());
        for (Map.Entry<String, Date> e : monitorLivingMap.entrySet()) {
            MonitorVo monitorVo=new MonitorVo();
            String aid = e.getKey();
            Date createDate = e.getValue();
            VideoInfo video = videoInfoService.getOne(new QueryWrapper<VideoInfo>().eq("aweme_id",aid));
            monitorVo.setAid(aid).setStartDate(getFormat(createDate)).setTitle(video.getTitleInfo());
            monitorVos.add(monitorVo);
        }
        return monitorVos;
    }
    @Autowired
    VideoMonitoredMapper videoMonitoredMapper;
    /**
     * 这里要筛选出最早创建时间的监控数据
     * @return
     */
    @Override
    public List<MonitorVo> getList() {
        List<VideoMonitored> videoMonitoredList = videoMonitoredMapper.selectList(new QueryWrapper<VideoMonitored>());
        List<MonitorVo>monitorVoList=new ArrayList<>(videoMonitoredList.size());
        for (VideoMonitored videoMonitored:videoMonitoredList) {
            String aid = videoMonitored.getAid();
            Date leastTime = videoMonitored.getCreateTime();
            MonitorVo monitorVo=new MonitorVo();
            VideoInfo videoInfo = videoInfoService.getOne(new QueryWrapper<VideoInfo>().eq("aweme_id", aid));

            monitorVo.setTitle(videoInfo.getTitleInfo());
            monitorVo.setStartDate(getFormat(leastTime));
            monitorVo.setAid(aid);
            monitorVoList.add(monitorVo);
        }

        return monitorVoList;
    }

}
