package com.markyao.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.markyao.mapper.CommentDetailsMapper;
import com.markyao.mapper.CommentUserMapper;
import com.markyao.mapper.MonitorCommentDiggMapper;
import com.markyao.mapper.VideoMonitoredMapper;
import com.markyao.model.pojo.*;
import com.markyao.model.vo.MonitorVo;
import com.markyao.service.harvest.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

import static com.markyao.utils.DateFormatUtils.getFormat;

@SpringBootTest
public class TestForMonitor {

    @Autowired
    MonitorCommentDiggMapper monitorCommentDiggMapper;
    @Autowired
    CommentService commentService;
    @Autowired
    CommentDetailsMapper commentDetailsMapper;
    @Autowired
    CommentUserMapper commentUserMapper;


    /**
     * 查询监控成果
     */
    @Test
    void test1(){
        List<MonitorCommentDigg> monitorCommentDiggList = monitorCommentDiggMapper.
                selectList(new QueryWrapper<MonitorCommentDigg>().select("cdid").orderByDesc("create_time"));

        Set<Long>cdidSet=new HashSet<>();
        //保持有序性
        List<Long>cdids=new ArrayList<>();
        for (MonitorCommentDigg monitorCommentDigg : monitorCommentDiggList) {
            Long cdid = monitorCommentDigg.getCdid();
            if (!cdidSet.contains(cdid)){
                cdidSet.add(cdid);
                cdids.add(cdid);
            }
        }
        //根据这个来分页
        int page=0;
        int pageSize=10;
        Page<CommentDetails> selectPage=new Page<>(page,pageSize);
        commentDetailsMapper.selectPage(selectPage,new QueryWrapper<CommentDetails>().in("id",cdids));

        System.out.println(selectPage.getTotal());
        for (CommentDetails record : selectPage.getRecords()) {
            System.out.println(record);
        }
    }

    @Autowired
    CommentDetailService commentDetailService;
    /**
     *  通过aid 更新评论点赞量
     */
    @Test
    void test2(){
        String aid="7239650108734803260";
        //commentDetails
        commentDetailService.updateStatusForAid(aid);

    }
/**
     *  通过cdid 查询最新点赞&评论数
     */
//    @Test
//    void test3(){
//        String cdid="7239597213498756410";
//        //commentDetails
//        List<MonitorCommentDigg> monitorCommentDiggList = monitorCommentDiggMapper.
//                selectList(new QueryWrapper<MonitorCommentDigg>().eq("cdid", cdid).orderByDesc("create_time"));
//        if (monitorCommentDiggList!=null && monitorCommentDiggList.size()>0){
//            MonitorCommentDigg monitorCommentDigg = monitorCommentDiggList.get(0);
//
//        }
//
//    }

    /**
     * 处理那些 初始时间上点赞量异常的监控数据
     */
    @Test
    void t3(){
        //1.搜索监控表中所有的cdid
        List<MonitorCommentDigg> cdids = monitorCommentDiggMapper
                .selectList(new QueryWrapper<MonitorCommentDigg>().groupBy("cdid").select("cdid"));
        int sum=0;
        //2.搜索这些cdid下的监控数据里面，是否有重复的时间? 如果有重复的时间，那么取比较小的那个，大的删掉
        for (MonitorCommentDigg monitorCommentDigg : cdids) {
            Long cdid = monitorCommentDigg.getCdid();
            List<MonitorCommentDigg> monitorCommentDiggList = monitorCommentDiggMapper
                    .selectList(new QueryWrapper<MonitorCommentDigg>().eq("cdid", cdid).orderByAsc("create_time"));
            Map<Long,MonitorCommentDigg> timeMap=new HashMap<>();
            for (int i = 0; i < monitorCommentDiggList.size(); i++) {
                MonitorCommentDigg curDigg = monitorCommentDiggList.get(i);
                long time = curDigg.getCreateTime().getTime();
                if (timeMap.containsKey(time)){
                    System.out.println("发现重复时间: ");
                    MonitorCommentDigg digg = timeMap.get(time);
                    if (digg.getDigCount()<curDigg.getDigCount()){
                        System.out.println("需要更新..");
                        timeMap.put(time,digg);
                    }
                }else {
                    timeMap.put(time,curDigg);
                }
                int deletes = monitorCommentDiggMapper.delete(new QueryWrapper<MonitorCommentDigg>().eq("create_time", curDigg.getCreateTime()));
                System.out.println("删除了: "+deletes);
                sum+=deletes;
                System.out.println("再放入最小点赞量的进去~");
                monitorCommentDiggMapper.insert(timeMap.get(time));
            }
        }
        System.out.println("size: "+cdids.size());
        System.out.println("总共删除了: "+sum);
    }

    @Test
    void t4(){
//        for (Long selectAid : monitorCommentDiggMapper.selectAids()) {
//            System.out.println(selectAid);
//        }

        for (CdidMinLeast cdidMinLeast : monitorCommentDiggMapper.selectMinCreateTimeForCd()) {
            System.out.println(cdidMinLeast);
        }
    }
    @Autowired
    VideoInfoService videoInfoService;
    @Autowired
    VideoMonitoredMapper videoMonitoredMapper;

    /**
     * 填充视频的监控数据
     */
    @Test
    void t5(){

        List<CdidMinLeast> cdidMinLeasts = monitorCommentDiggMapper.selectMinCreateTimeForCd();
//
        Map<String,Date>aidMap=new HashMap<>();
        for (CdidMinLeast cdidMinLeast : cdidMinLeasts) {
            Long cdid = cdidMinLeast.getCdid();
            Date createTime = cdidMinLeast.getCreateTime();
            CommentDetails commentDetails = commentDetailsMapper.selectOne(new QueryWrapper<CommentDetails>().eq("id",cdid).select("aweme_id"));
            if (commentDetails==null){
                continue;
            }
            String awemeId = commentDetails.getAwemeId();
            if (aidMap.containsKey(awemeId)){
                if (createTime.getTime()<aidMap.get(awemeId).getTime()){
                    aidMap.put(awemeId,createTime);
                }
            }else {
                aidMap.put(awemeId,createTime);
            }
        }
        int sum=0;
        List<VideoMonitored>videoMonitoredList=new ArrayList<>(aidMap.size());
        for (Map.Entry<String, Date> e : aidMap.entrySet()) {
            String aid = e.getKey();
            Date leastTime = e.getValue();

            VideoMonitored videoMonitored=new VideoMonitored();
            videoMonitored.setId(0l).setAid(aid).setCreateTime(leastTime);
            videoMonitoredMapper.insert(videoMonitored);
            sum++;
//            videoMonitoredList.add(videoMonitored);
        }
        System.out.println(sum);


    }
}
