package com.markyao.async;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.markyao.mapper.HarvestCommentUrlMapper;
import com.markyao.mapper.VideoInfoMapper;
import com.markyao.mapper.VideoMonitoredMapper;
import com.markyao.model.pojo.HarvestCommentUrl;
import com.markyao.model.pojo.VideoInfo;
import com.markyao.model.pojo.VideoMonitored;
import com.markyao.service.MonitorDigCountService;
import com.markyao.service.harvest.CommentService;
import com.markyao.service.harvest.RequestURLService;
import com.markyao.service.harvest.impl.RequestURLServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.markyao.common.States.MONITORING;

@Component
@Slf4j
public class ThreadService {
    @Autowired
    HarvestCommentUrlMapper harvestCommentUrlMapper;
    @Autowired
    ConcurrentHashMap<String,Object> stateMap;
    @Autowired
    VideoMonitoredMapper videoMonitoredMapper;
    @Async("taskExecutor")
    public void updateUrlStatus(HarvestCommentUrlMapper harvestCommentUrlMapper, HarvestCommentUrl harvestCommentUrl,String aid) {
        harvestCommentUrl.setIsDeal(true);
        harvestCommentUrl.setVideoId(aid);
        harvestCommentUrlMapper.updateById(harvestCommentUrl);
        log.info("更新url收集状态..");
    }


   /* @Async("taskExecutor")
    public void startHarvestUrl(String url, RequestURLService requestURLService,int isAllHarvest) {
        log.info("异步收集~");
        requestURLService.start0(url,1,isAllHarvest);
    }*/

    @Async("taskExecutor")
    public void startHarvestAllComments(CommentService commentService) {
        log.info("异步采集评论数据~~");
        commentService.startHarvestAllComments();
    }
    @Async("taskExecutor")
    public void startMonitor(String aid, int timeStand, MonitorDigCountService monitorDigCountService) throws IOException, ParseException {
        log.info("异步监控评论数据~~ aid:{} ",aid);

        monitorDigCountService.monitorForDigCount2(aid,timeStand);

    }

    @Async("taskExecutor")
    public void deleteOldUrls() {
        harvestCommentUrlMapper.delete(new QueryWrapper<HarvestCommentUrl>().eq("is_deal",1));
        log.info("异步删除旧数据~~");
    }
    @Async("taskExecutor")
    public void updateVideInfo(VideoInfoMapper videoInfoMapper,String aid) {
        VideoInfo videoInfo = videoInfoMapper.selectOne(new QueryWrapper<VideoInfo>().eq("aweme_id", aid));
        if (videoInfo.getCanMonitor()==null || !videoInfo.getCanMonitor()){
            videoInfo.setCanMonitor(true);
            videoInfoMapper.updateById(videoInfo);
            log.info("异步更新视频信息~~");
        }
    }


    public void sleep(int s){
        try {
            Thread.sleep(s*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Async("taskExecutor")
    public void startHarvest(RequestURLServiceImpl requestURLService,String searchVal) {
        requestURLService.startHarvest(searchVal);
    }
}