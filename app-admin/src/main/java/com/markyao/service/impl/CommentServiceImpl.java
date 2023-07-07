package com.markyao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.markyao.mapper.*;
import com.markyao.model.dto.RestData;
import com.markyao.model.pojo.*;
import com.markyao.model.vo.CommentDetailsVo;
import com.markyao.model.vo.CommentVo;
import com.markyao.model.vo.PageVo;
import com.markyao.model.vo.VideoInfoVo;
import com.markyao.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.markyao.common.States.*;

@Service
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper,Comment> implements CommentService {
    @Autowired
    CommentUserMapper commentUserMapper;
    @Autowired
    CommentDetailsMapper commentDetailsMapper;
    @Autowired
    CommentMapper commentMapper;
    @Value("${douyin.user.cardPrefix}")
    String cardUrlPrefix;
//    @Value("${douyin.video.searchLinkPrefix}")
//    String searchLinkPrefix;
//    @Autowired
//    HarvestCommentUrlMapper harvestCommentUrlMapper;
//    @Autowired
//    ConcurrentHashMap<String,Object>stateMap;
    @Autowired
    VideoInfoMapper videoInfoMapper;
    @Autowired
    MonitorCommentDiggMapper monitorCommentDiggMapper;
//
//    @Autowired
//    ConcurrentHashMap<String,Object>bufMap;
//    @Autowired
//    HarvestCommentWorker harvestCommentWorker;
//    @Autowired
//    VideoInfoService videoInfoService;

    /**
     * 分页查询,所有评论数据.
     * @param current
     * @param pageSize
     * @return
     */
    @Override
    public RestData pages(int current, int pageSize){
        Page<Comment>selectPage=new Page<>(current,pageSize);
        commentMapper.selectPage(selectPage,null);
        List<Comment> records = selectPage.getRecords();
        List<CommentVo> collect = records.stream().map(c -> {
            Long uid = c.getUid();
            Long did = c.getDid();
            CommentDetails commentDetails = commentDetailsMapper.selectById(did);
            CommentDetailsVo vo=new CommentDetailsVo();
            vo.setCreateTime(getFormatDate(commentDetails.getCreateTime()));
            BeanUtils.copyProperties(commentDetails,vo);
            CommentUser commentUser = commentUserMapper.selectById(uid);
            CommentVo commentVo = new CommentVo();
            commentVo.setId(c.getId());
            commentVo.setCommentUser(commentUser);
            commentVo.setCommentDetails(vo);
            commentVo.setUserCardLink(cardUrlPrefix + commentUser.getSecUid());
            return commentVo;
        }).collect(Collectors.toList());
        PageVo pageVo=new PageVo();
        pageVo.setList(collect);
        pageVo.setTotals(selectPage.getTotal());
        pageVo.setTotalPages(selectPage.getPages());
        return RestData.success(pageVo);
    }



/**
     * 分页查询，并且还要加上视频信息
     * @param awemeId
     * @param current
     * @param pageSize
     * @return
     */
    @Override
    public RestData pages(String awemeId, int current, int pageSize, int searchType, Object searchMsg,String sortType) {
        VideoInfo videoInfo = videoInfoMapper.selectOne(new QueryWrapper<VideoInfo>().eq("aweme_id", awemeId));
        VideoInfoVo videoInfoVo=new VideoInfoVo();
        BeanUtils.copyProperties(videoInfo,videoInfoVo);

        Page<CommentDetails>selectPage=new Page<>(current,pageSize);
        List<CommentVo> collect=null;
        if (sortType.equals(ORDER_IP) || sortType.equals(ORDER_DATE) || sortType.equals(ORDER_DIGGCOUNT) || sortType.equals(ORDER_REPLYOUNT)){
            String sortField = "";
            boolean isASC=true;
            if (sortType.equals(ORDER_IP)){
                sortField="ip_label";
            }else if (sortType.equals(ORDER_DATE)){
                sortField="create_time";
            }else if (sortType.equals(ORDER_DIGGCOUNT)){
                sortField="digg_count";
                isASC=false;
            }else if (sortType.equals(ORDER_REPLYOUNT)){
                sortField="reply_comment_total";
                isASC=false;
            }
            //排序
            if (searchType==S_QUERY_SEARCH ){
                List<Long> ids = commentDetailsMapper.searchByWordSort(awemeId, searchMsg.toString(), sortField,current,pageSize);
                if (isASC){
                    commentDetailsMapper.selectPage(selectPage,new QueryWrapper<CommentDetails>().eq("aweme_id",awemeId).in("id",ids).orderByAsc(sortField));
                }else {
                    commentDetailsMapper.selectPage(selectPage,new QueryWrapper<CommentDetails>().eq("aweme_id",awemeId).in("id",ids).orderByDesc(sortField));
                }
            }else if (searchType==S_ID_SEARCH){
                commentDetailsMapper.selectPage(selectPage,new QueryWrapper<CommentDetails>().eq("aweme_id",awemeId));
            }else if (searchType==S_IP_SEARCH ){
                List<Long> ids = commentDetailsMapper.searchByIpLabel(awemeId, searchMsg.toString());
                if (isASC){
                    commentDetailsMapper.selectPage(selectPage,new QueryWrapper<CommentDetails>().eq("aweme_id",awemeId).in("id",ids).orderByAsc(sortField));
                }else {
                    commentDetailsMapper.selectPage(selectPage,new QueryWrapper<CommentDetails>().eq("aweme_id",awemeId).in("id",ids).orderByDesc(sortField));
                }
            }else if (searchType==S_DATE_SEARCH ){
                String startDateTimeStr = searchMsg.toString()+":00";
                String eneDateTimeStr = searchMsg.toString()+":59";
                List<Long> ids = commentDetailsMapper.selectList(new QueryWrapper<CommentDetails>().select("id")
                        .ge("create_time",startDateTimeStr).le("create_time",eneDateTimeStr)).stream().map(cd->cd.getId()).collect(Collectors.toList());
                if (isASC){
                    commentDetailsMapper.selectPage(selectPage,new QueryWrapper<CommentDetails>().eq("aweme_id",awemeId).in("id",ids).orderByAsc(sortField));
                }else {
                    commentDetailsMapper.selectPage(selectPage,new QueryWrapper<CommentDetails>().eq("aweme_id",awemeId).in("id",ids).orderByDesc(sortField));
                }
            }
            else {
                if (isASC){
                    commentDetailsMapper.selectPage(selectPage,new QueryWrapper<CommentDetails>().eq("aweme_id",awemeId).orderByAsc(sortField));
                }else {
                    commentDetailsMapper.selectPage(selectPage,new QueryWrapper<CommentDetails>().eq("aweme_id",awemeId).orderByDesc(sortField));
                }
            }
        }
        else {
            if (searchType==S_QUERY_SEARCH ){
                List<Long> ids = commentDetailsMapper.searchByWord(awemeId, searchMsg.toString());
                commentDetailsMapper.selectPage(selectPage,new QueryWrapper<CommentDetails>().eq("aweme_id",awemeId).in("id",ids));
            }else if (searchType==S_ID_SEARCH){
                commentDetailsMapper.selectPage(selectPage,new QueryWrapper<CommentDetails>().eq("aweme_id",awemeId));
            }else if (searchType==S_IP_SEARCH ){
                List<Long> ids = commentDetailsMapper.searchByIpLabel(awemeId, searchMsg.toString());
                commentDetailsMapper.selectPage(selectPage,new QueryWrapper<CommentDetails>().eq("aweme_id",awemeId).in("id",ids));
            }else if (searchType==S_DATE_SEARCH ){
                String startDateTimeStr = searchMsg.toString()+":00";
                String eneDateTimeStr = searchMsg.toString()+":59";
                List<Long> ids = commentDetailsMapper.selectList(new QueryWrapper<CommentDetails>().select("id")
                        .ge("create_time",startDateTimeStr).le("create_time",eneDateTimeStr)).stream().map(cd->cd.getId()).collect(Collectors.toList());
                commentDetailsMapper.selectPage(selectPage,new QueryWrapper<CommentDetails>().eq("aweme_id",awemeId).in("id",ids).orderByAsc("create_time"));
            }
            else {
                commentDetailsMapper.selectPage(selectPage,new QueryWrapper<CommentDetails>().eq("aweme_id",awemeId));
                if (videoInfo.getTotals()==null || videoInfo.getTotals()<selectPage.getTotal()){
                    videoInfo.setTotals(Math.toIntExact(selectPage.getTotal()));
                    log.info("更新视频评论总数~");
                    videoInfoMapper.updateById(videoInfo);
                }
            }
        }



        List<CommentDetails> records = selectPage.getRecords();


        collect = records.stream().map(cd -> {
            Long dId = cd.getId();
            Comment c = commentMapper.selectOne(new QueryWrapper<Comment>().eq("did", dId));
            CommentDetailsVo commentDetailsVo=new CommentDetailsVo();
            BeanUtils.copyProperties(cd,commentDetailsVo);
            commentDetailsVo.setId(dId+"");
            commentDetailsVo.setCreateTime(getFormatDate(cd.getCreateTime()));
            CommentUser commentUser = commentUserMapper.selectById(c.getUid());
            CommentVo commentVo = new CommentVo();
            commentVo.setId(c.getId());
            commentVo.setCommentUser(commentUser);
            commentVo.setCommentDetails(commentDetailsVo);
            commentVo.setUserCardLink(cardUrlPrefix + commentUser.getSecUid());
            //检查是否可以去看监控数据
            Integer cnt=-1;
            try {
                cnt = monitorCommentDiggMapper.selectCount(new QueryWrapper<MonitorCommentDigg>().eq("cdid", dId));
            } catch (Exception e) {cnt=-1;}
            if (cnt!=-1 && cnt>0){
                commentVo.setIsMonitored(true);
            }else {
                commentVo.setIsMonitored(false);
            }
            return commentVo;
        }).collect(Collectors.toList());
        PageVo pageVo=new PageVo();
        pageVo.setList(collect);
        pageVo.setTotals(selectPage.getTotal());
        pageVo.setTotalPages(selectPage.getPages());
        pageVo.setInfo(videoInfoVo);
        return RestData.success(pageVo);
    }

    private static String getFormatDate(Date createTime) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sdf.format(createTime);
    }


    /*
        获取视频的评论信息
     */
   /* @Override
    public void startHarvestAllComments() {
        if (bufMap.containsKey(CUR_HARVEST_COMMENTS_CNT)){
            bufMap.put(CUR_HARVEST_COMMENTS_CNT,0);
        }
        if (stateMap.containsKey(STOP_HARVEST_COMMENTS)){
            stateMap.remove(STOP_HARVEST_COMMENTS);
        }
        int start=1;
        Page<HarvestCommentUrl>page=new Page<>(start,5);
        Page<HarvestCommentUrl> harvestCommentUrlPage = harvestCommentUrlMapper.selectPage(page, new QueryWrapper<HarvestCommentUrl>());
        List<HarvestCommentUrl> commentList = harvestCommentUrlPage.getRecords();
        long total = page.getTotal();
        long totalPages = total / 5+1;
        batchHarvestComments(commentList);
        while (++start<totalPages){
            page=new Page<>(start,5);
            harvestCommentUrlPage = harvestCommentUrlMapper.selectPage(page, new QueryWrapper<HarvestCommentUrl>());
            commentList = harvestCommentUrlPage.getRecords();
            batchHarvestComments(commentList);
        }
        if (bufMap.containsKey(CUR_HARVEST_COMMENTS_CNT)){
            bufMap.put(CUR_HARVEST_COMMENTS_CNT,0);
        }
    }*/


/*
    private void batchHarvestComments(List<HarvestCommentUrl> commentList) {
        commentList.stream().forEach(c->{
            if (!c.getIsDeal()){
                String requestUrl = c.getUrl();
                try {
                    CommentService commentService = (CommentService) AopContext.currentProxy();
                    Thread.sleep(5000);
                    if (!stateMap.containsKey(STOP_HARVEST_COMMENTS)){
                        if (stateMap.containsKey(STOP_HARVEST_COMMENTS)){
                            return;
                        }else {
                            commentService.harvestComments(requestUrl,c);
                        }
                    }else {
                        if ((int) stateMap.get(STOP_HARVEST_COMMENTS)==2){
                            return;
                        }
                        long pauseTime=5*1000;
                        while (stateMap.containsKey(STOP_HARVEST_COMMENTS)){
                            if ((int) stateMap.get(STOP_HARVEST_COMMENTS)==2){
                                return;
                            }
                            Thread.sleep(pauseTime);
                            pauseTime*=2;
                        }
                        if ((int) stateMap.get(STOP_HARVEST_COMMENTS)==2){
                            return;
                        }
                        commentService.harvestComments(requestUrl,c);

                    }
                } catch (IOException |ParseException | InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        });
    }*/


    /**
     * 注意,cookie最好常换，而且要使用登录的账户的cookie
     * @param url
     * @throws IOException
     * @throws ParseException
     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void harvestComments(String url,HarvestCommentUrl c) {
//        LinkedHashMap<String, Object> stringObjectLinkedHashMap = null;
//        try {
//            stringObjectLinkedHashMap = harvestCommentWorker.getCommentsResponse(url);
//        } catch (IOException |ParseException e) {
//            e.printStackTrace();
//            return;
//        }
//        int cnt=0;
//        String aid="";
//        for (Map.Entry<String, Object> e : stringObjectLinkedHashMap.entrySet()) {
//            if (e.getKey().equals("comments")){
//                ArrayList<Map> value = (ArrayList) e.getValue();
//                if (null == value){
//                    break;
//                }
//                for (Map map : value) {
//                    if (StringUtils.isEmpty(aid)){
//                        aid=map.get("aweme_id").toString();
//                    }
//                    CommentDetails commentDetails=fillCommentDetails(map);
//                    commentDetailsMapper.insertForId(commentDetails);
//                    CommentUser commentUser=fillCommentUser(map);
//                    commentUserMapper.insertForId(commentUser);
//                    Comment comment=new Comment();
//                    comment.setAid(commentDetails.getAwemeId());
//                    comment.setUid(commentUser.getId());
//                    comment.setDid(commentDetails.getId());
//                    commentMapper.insert(comment);
//                    cnt++;
//                }
//            }
//        }
//        threadService.updateUrlStatus(harvestCommentUrlMapper,c,aid);
//        threadService.updateVideInfo(videoInfoMapper,aid);
//        bufMap.put(CUR_HARVEST_COMMENTS_CNT,(int)bufMap.getOrDefault(CUR_HARVEST_COMMENTS_CNT,0)+cnt);
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void harvestComments(LinkedHashMap<String, Object> stringObjectLinkedHashMap,HarvestCommentUrl hcu) {
//        int cnt=0;
//        String aid="";
//        for (Map.Entry<String, Object> e : stringObjectLinkedHashMap.entrySet()) {
//            if (e.getKey().equals("comments")){
//                ArrayList<Map> value = (ArrayList) e.getValue();
//                if (null == value){
//                    break;
//                }
//                for (Map map : value) {
//                    if (StringUtils.isEmpty(aid)){
//                        aid=map.get("aweme_id").toString();
//                    }
//                    CommentDetails commentDetails=fillCommentDetails(map);
//                    commentDetailsMapper.insertForId(commentDetails);
//                    CommentUser commentUser=fillCommentUser(map);
//                    commentUserMapper.insertForId(commentUser);
//                    Comment comment=new Comment();
//                    comment.setAid(commentDetails.getAwemeId());
//                    comment.setUid(commentUser.getId());
//                    comment.setDid(commentDetails.getId());
//                    commentMapper.insert(comment);
//                    cnt++;
//                }
//            }
//        }
//        bufMap.put(CUR_HARVEST_COMMENTS_CNT,(int)bufMap.getOrDefault(CUR_HARVEST_COMMENTS_CNT,0)+cnt);
//        updateUrlStatus(harvestCommentUrlMapper,hcu,aid);
//        updateVideInfo(videoInfoMapper,aid);
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void harvestComments(LinkedHashMap<String, Object> stringObjectLinkedHashMap) throws IOException, ParseException {
//        int cnt=0;
//        String aid="";
//        int cur= (int) stringObjectLinkedHashMap.get("thisCur");
//        for (Map.Entry<String, Object> e : stringObjectLinkedHashMap.entrySet()) {
//            if (e.getKey().equals("comments")){
//                ArrayList<Map> value = (ArrayList) e.getValue();
//                if (null == value){
//                    break;
//                }
//                for (Map map : value) {
//                    if (StringUtils.isEmpty(aid)){
//                        aid=map.get("aweme_id").toString();
//                    }
//                    CommentDetails commentDetails=fillCommentDetails(map);
//                    commentDetails.setCur(cur).setCount(50);
//                    CommentUser commentUser=fillCommentUser(map);
//                    Comment comment=new Comment();
//                    comment.setAid(commentDetails.getAwemeId());
//                    comment.setUid(commentUser.getId());
//                    comment.setDid(commentDetails.getId());
//                    commentDetailsMapper.insertForId(commentDetails);
//                    commentUserMapper.insertForId(commentUser);
//                    commentMapper.insert(comment);
//                    cnt++;
//                }
//            }
//        }
//        bufMap.put(CUR_HARVEST_COMMENTS_CNT,(int)bufMap.getOrDefault(CUR_HARVEST_COMMENTS_CNT,0)+cnt);
//        updateVideInfo(videoInfoMapper,aid);
//    }
//
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void saveCommentTransaction(CommentDetails commentDetails,CommentUser commentUser,Comment comment){
//        commentDetailsMapper.insertForId(commentDetails);
//        commentUserMapper.insertForId(commentUser);
//        commentMapper.insert(comment);
//    }

    /**
     * 通过deatails表的cid 去重
     * @param cid
     */
//    @Override
//    public void duplicateDealByCid(String cid) {
//        //保留最老的数据
//        List<CommentDetails> commentDetails = commentDetailsMapper.selectList(new QueryWrapper<CommentDetails>().eq("cid", cid).orderByAsc("create_time"));
//        if (commentDetails.size()<=1){
//            return;
//        }
//        List<Long>ids=new ArrayList<>(commentDetails.size());
//        List<Long>dids=new ArrayList<>(commentDetails.size());
//        List<Long>uids=new ArrayList<>(commentDetails.size());
//        //保留第一个
//        for (int i=1;i<commentDetails.size();i++){
//            CommentDetails detail = commentDetails.get(i);
//            Comment comment = commentMapper.selectOne(new QueryWrapper<Comment>().eq("did", detail.getId()));
//            dids.add(detail.getId());
//            if(comment==null){
//                continue;
//            }
//            ids.add(comment.getId());
//            try {
//                uids.add(comment.getUid());
//            } catch (Exception e) {}
//        }
//        CommentService o = (CommentService) AopContext.currentProxy();
//        o.batchDeleteThreeTb(ids,dids,uids);
//        log.info("去重成功~~ 干掉 {} 个重复项",commentDetails.size());
//    }
//
//    @Override
//    @Transactional
//    public void deleteThreeTb(Long id, Long did, Long uid) {
//        commentMapper.deleteById(id);
//        commentDetailsMapper.deleteById(did);
//        commentUserMapper.deleteById(uid);
//    }
//
//    @Override
//    @Transactional
//    public void batchDeleteThreeTb(List<Long> ids, List<Long> dids, List<Long> uids) {
//        commentMapper.deleteBatchIds(ids);
//        commentDetailsMapper.deleteBatchIds(dids);
//        commentUserMapper.deleteBatchIds(uids);
//    }

//    @Transactional(rollbackFor = Exception.class)
//    public void updateVideInfo(VideoInfoMapper videoInfoMapper, String aid) {
//        VideoInfo videoInfo = videoInfoMapper.selectOne(new QueryWrapper<VideoInfo>().eq("aweme_id", aid));
//        if (videoInfo==null){
//            videoInfo=videoInfoService.getVideo(aid);
//            videoInfoService.save(videoInfo);
//        }
//        if (videoInfo.getCanMonitor()==null || !videoInfo.getCanMonitor()){
//            videoInfo.setCanMonitor(true);
//            videoInfoMapper.updateById(videoInfo);
//            log.info("更新视频信息~~");
//        }
//    }
//
//    private void updateUrlStatus(HarvestCommentUrlMapper harvestCommentUrlMapper, HarvestCommentUrl hcu, String aid) {
//        hcu.setIsDeal(true);
//        hcu.setVideoId(aid);
//        harvestCommentUrlMapper.updateById(hcu);
//        log.info("更新url收集状态..");
//    }
//
//
//    private String dealUrl(String url) {
//        String s = url.replaceFirst("&count=20", "");
//        String ns=s.substring(0,s.indexOf("&cursor"))+"&count=20"+s.substring(s.indexOf("&cursor"));
//        return ns;
//    }
//
//    private static String unicoderString(String str){
//        String regex = "\\\\x[0-9a-fA-F]{2}";
//        return str.replaceAll(regex, "emoji");
//    }
//
//
//
//    public static long targetTime(long time){
//        return time * 1000L;
//    }
//    public static String getTargetTime(long time){
//     try {
//         // 将时间戳转换为毫秒
//         long timestampInMillis = time * 1000L;
//
//         // 创建Date对象
//         Date date = new Date(timestampInMillis);
//
//         // 创建SimpleDateFormat对象，指定日期和时间格式
//         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//         // 格式化日期和时间，并返回字符串
//         return sdf.format(date);
//     } catch (Exception e) {
//         e.printStackTrace();
//         return "";
//     }
//    }
//



}
