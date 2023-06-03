package com.markyao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.markyao.async.BoomWorkers;
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
import com.markyao.service.harvest.CommentService;
import com.markyao.service.harvest.core.HarvestCommentWorker;
import com.markyao.utils.DateFormatUtils;
import com.markyao.utils.MonitorPowerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.IbatisException;
import org.apache.ibatis.exceptions.TooManyResultsException;
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
    HarvestCommentWorker harvestCommentWorker;
    @Autowired
    HarvestCommentUrlMapper harvestCommentUrlMapper;
    @Autowired
    CommentDetailsMapper commentDetailsMapper;
    /*
        老接口,已弃用
     */
    @Override
    public void monitorForDigCount(String aid,int timeStand) throws IOException, ParseException {
        List<HarvestCommentUrl> urls = harvestCommentUrlMapper.
                selectList(new QueryWrapper<HarvestCommentUrl>().eq("video_id", aid).select("is_deal", "url"));

        for (HarvestCommentUrl url : urls) {
            if (url.getIsDeal()){
                LinkedHashMap<String, Object> response = harvestCommentWorker.getCommentsResponse(url.getUrl());
                sleep(timeStand);
                for (Map.Entry<String, Object> e : response.entrySet()) {
                    if (e.getKey().equals("comments")){
                        ArrayList<Map> value = (ArrayList) e.getValue();
                        if (null == value){
                            break;
                        }
                        for (Map map : value) {
                            String cid = map.get("cid").toString();
                            CommentDetails commentDetails = commentDetailsMapper.selectOne(new QueryWrapper<CommentDetails>().eq("cid", cid));
                            checkOk(commentDetails,map,0);
                        }
                    }
                }
            }
        }
    }
    @Autowired
    ConcurrentHashMap<String,Object> stateMap;

    @Override
    public void monitorForDigCount2(String aid,int timeStand) throws IOException, ParseException {
        checkVideoMonitored(aid);
        boolean isNext=true;
        int cur=0;//当前条数
        while (stateMap.containsKey(MONITORING)){
            HashSet monitoringSet = (HashSet) stateMap.getOrDefault(MONITORING, new HashSet<String>());
            if (monitoringSet.contains(aid)){
                LinkedHashMap<String, Object> response = harvestCommentWorker.getCommentsResponseForAid(aid,cur);
                BigInteger has_more = (BigInteger) response.get("has_more");
                BigInteger cursor = (BigInteger) response.get("cursor");
                cur= cursor.intValue();
                if (has_more.intValue()==0){
                    log.info("本轮评论已收集完毕~进入下一轮");
                    cur=0;
                }else {
                    log.info("当前抓取页/条 {} ",cur);
                    startMonitoring(timeStand, cur, response);
                }
            }else {
                log.info("监控aid :{} 停止",aid);
                return;
            }
        }


    }

    private void startMonitoring(int timeStand, int cur, LinkedHashMap<String, Object> response) {
        for (Map.Entry<String, Object> e : response.entrySet()) {
            if (e.getKey().equals("comments")){
                ArrayList<Map> value = (ArrayList) e.getValue();
                if (null == value){
                    break;
                }
                for (Map map : value) {
                    String cid = map.get("cid").toString();
                    CommentDetails commentDetails = null;
                    try {
                        commentDetails = commentDetailsMapper.selectOne(new QueryWrapper<CommentDetails>().eq("cid", cid));
                    } catch (Exception ex) {
                        //去重
                        log.info("去重~ ~ ~");
                        commentService.duplicateDealByCid(cid);
                        commentDetails = commentDetailsMapper.selectOne(new QueryWrapper<CommentDetails>().eq("cid", cid));
                    }
                    if (commentDetails!=null){
                        if (commentDetails.getCur()==null || commentDetails.getCur()==0){
                            commentDetails.setCur(cur);
                            commentDetailsMapper.updateById(commentDetails);
                        }
                        checkOk(commentDetails,map,cur);
                    }else {
                        log.info("监控发现新增数据~");
                        commentDetails=fillCommentDetails(map);
                        commentDetails.setCur(cur);
                        CommentUser commentUser=fillCommentUser(map);
                        Comment comment=new Comment();
                        comment.setAid(commentDetails.getAwemeId());
                        comment.setUid(commentUser.getId());
                        comment.setDid(commentDetails.getId());
                        commentService.saveCommentTransaction(commentDetails,commentUser,comment);
                    }
                }
            }
        }
        sleep(timeStand);
    }

    /**
     * 固定cur监视数据
     * @param aid
     * @param cur
     * @param timeStand
     * @throws IOException
     * @throws ParseException
     */
    @Override
    public void monitorForDigCount3(String aid,int cur,int timeStand) throws IOException, ParseException {
        checkVideoMonitored(aid);
        LinkedHashMap<String, Object> response = harvestCommentWorker.getCommentsResponseForAid(aid,cur);
        BigInteger has_more = (BigInteger) response.get("has_more");
        startMonitoring(timeStand, cur, response);
    }

    private void checkVideoMonitored(String aid) {
        if (videoMonitoredMapper.selectOne
                (new QueryWrapper<VideoMonitored>().eq("aid", aid))==null){
            VideoMonitored videoMonitored = new VideoMonitored();
            videoMonitored.setCreateTime(new Date()).setAid(aid).setId(0L).setVersion(1);
            videoMonitoredMapper.insert(videoMonitored);
        }
    }


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

    @Override
    public List<MonitorPowerVo> getBoomList(ConcurrentHashMap<String, Object> monitorPowerMap) {
        Set powerSet = (HashSet) monitorPowerMap.get(States.POWER_MONITORING);
        List<MonitorPowerVo>monitorPowerVoList=new ArrayList<>(powerSet.size());
        for (Object key : powerSet) {
            String cur = MonitorPowerUtils.getCur(key.toString());
            String aid = MonitorPowerUtils.getAid(key.toString());
            String did = MonitorPowerUtils.getDid(key.toString());
            VideoInfo videoInfo = videoInfoService.getOne(new QueryWrapper<VideoInfo>().eq("aweme_id", aid));
            CommentDetails commentDetails = commentDetailsMapper.selectById(did);
            VideoInfoVo videoInfoVo=new VideoInfoVo();
            BeanUtils.copyProperties(videoInfo,videoInfoVo);
            videoInfoVo.setId(videoInfo.getId()+"");

            CommentDetailsVo commentDetailsVo=new CommentDetailsVo();
            BeanUtils.copyProperties(commentDetails,commentDetailsVo);
            commentDetailsVo.setId(commentDetails.getId()+"");
            commentDetailsVo.setCreateTime(DateFormatUtils.getFormat(commentDetails.getCreateTime()));

            MonitorPowerVo monitorPowerVo=new MonitorPowerVo();
            monitorPowerVo.setCur(cur).setAid(aid).setCommentDetailsVo(commentDetailsVo).setVideoInfoVo(videoInfoVo);
            monitorPowerVoList.add(monitorPowerVo);
        }
        return monitorPowerVoList;
    }

    private void sleep(int timeStand){
        try {
            Thread.sleep(timeStand * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Autowired
    CommentService commentService;
    @Autowired
    BoomWorkers boomWorkers;


    private static final int boomSize=200;
    @Autowired
    MonitorCommentDiggMapper monitorCommentDiggMapper;
    private void checkOk(CommentDetails commentDetails, Map map,int cur) {
        long cdid=commentDetails.getId();
        MonitorCommentDigg monitorDetails = null;
        try {
            monitorDetails = monitorCommentDiggMapper.selectOne(new QueryWrapper<MonitorCommentDigg>().eq("cdid", cdid));
        } catch (Exception e) {}

        int digg_count = Integer.parseInt(map.get("digg_count").toString());
        int reply_comment_total = Integer.parseInt(map.get("reply_comment_total").toString());
        Boolean is_author_digged = (Boolean) map.get("is_author_digged");

        int curDiggcnt;
        int replyCnt;
        if (monitorDetails!=null){
            curDiggcnt=monitorDetails.getDigCount();
            replyCnt=monitorDetails.getReplyCommentTotal();
        }else {
            curDiggcnt=commentDetails.getDiggCount();
            replyCnt=commentDetails.getReplyCommentTotal();
        }


        if (digg_count!=curDiggcnt ||reply_comment_total!=replyCnt){
            log.info("监控发现评论情况变化~~");
            //todo 如果发现有那种"爆炸"评论，即增量极大的，调用线程池单独为其加紧监控
            /*if ((digg_count-curDiggcnt)>=boomSize || (reply_comment_total-replyCnt)>=50){
                Set powerSet = (Set) monitorPowerMap.getOrDefault(States.POWER_MONITORING,new HashSet<String>());
                powerSet.add(commentDetails.getAwemeId()+"::"+cur+"::"+commentDetails.getId());
                monitorPowerMap.put(States.POWER_MONITORING,powerSet);
                boomWorkers.boomCommentMonitor(cur,commentDetails.getAwemeId(),commentDetails.getId()+"");
            }*/
            MonitorCommentDigg monitorCommentDigg = new MonitorCommentDigg();
            monitorCommentDigg.setCdid(commentDetails.getId());
            monitorCommentDigg.setDigCount(digg_count);
            monitorCommentDigg.setReplyCommentTotal(reply_comment_total);
            monitorCommentDigg.setIsAuthorDigged(is_author_digged);
            monitorCommentDigg.setCreateTime(new Date());
            this.save(monitorCommentDigg);
        }
    }

    @Autowired
    ConcurrentHashMap<String, Object>monitorPowerMap;
}
