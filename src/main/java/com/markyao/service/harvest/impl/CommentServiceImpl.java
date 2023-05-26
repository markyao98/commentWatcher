package com.markyao.service.harvest.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.markyao.async.ThreadService;
import com.markyao.mapper.*;
import com.markyao.model.dto.RestData;
import com.markyao.model.pojo.*;
import com.markyao.model.vo.CommentDetailsVo;
import com.markyao.model.vo.CommentVo;
import com.markyao.model.vo.PageVo;
import com.markyao.model.vo.VideoInfoVo;
import com.markyao.service.harvest.CommentService;
import com.markyao.service.impl.AnalyzerServiceImpl;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
    @Value("${douyin.video.searchLinkPrefix}")
    String searchLinkPrefix;
    @Autowired
    HarvestCommentUrlMapper harvestCommentUrlMapper;


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


    @Autowired
    VideoInfoMapper videoInfoMapper;
    /**
     * 分页查询，并且还要加上视频信息
     * @param awemeId
     * @param current
     * @param pageSize
     * @return
     */
//    @Override
//    public RestData pages(String awemeId, int current, int pageSize, int searchType, Object searchMsg,String sortType) {
//        VideoInfo videoInfo = videoInfoMapper.selectOne(new QueryWrapper<VideoInfo>().eq("aweme_id", awemeId));
//        VideoInfoVo videoInfoVo=new VideoInfoVo();
//        BeanUtils.copyProperties(videoInfo,videoInfoVo);
//
//        Page<Comment>selectPage=new Page<>(current,pageSize);
//        List<CommentVo> collect=null;
//        if (sortType.equals(ORDER_IP)){
//            //根据ip排序
//            if (searchType==S_QUERY_SEARCH ){
//                List<Long> ids = commentDetailsMapper.searchByWordSort(awemeId, searchMsg.toString(),"ip_label",current,pageSize);
//                commentMapper.selectPage(selectPage,new QueryWrapper<Comment>().eq("aid",awemeId).in("did",ids));
//            }else if (searchType==S_ID_SEARCH){
//                commentMapper.selectPage(selectPage,new QueryWrapper<Comment>().eq("aid",awemeId));
//            }else if (searchType==S_IP_SEARCH ){
//                List<Long> ids = commentDetailsMapper.searchByIpLabelSort(awemeId, searchMsg.toString(),"ip_label",current,pageSize);
//                commentMapper.selectPage(selectPage,new QueryWrapper<Comment>().eq("aid",awemeId).in("did",ids));
//            }
//            else {
//                List<Long> ids = commentDetailsMapper.selectList(new QueryWrapper<CommentDetails>().eq("aweme_id",awemeId).orderByAsc("ip_label").select("id"))
//                        .stream().map(d -> d.getId()).collect(Collectors.toList());
//                commentMapper.selectPage(selectPage,new QueryWrapper<Comment>().eq("aid",awemeId).in("did",ids));
//            }
//        }
//        else {
//            if (searchType==S_QUERY_SEARCH ){
//                List<Long> ids = commentDetailsMapper.searchByWord(awemeId, searchMsg.toString());
//                commentMapper.selectPage(selectPage,new QueryWrapper<Comment>().eq("aid",awemeId).in("did",ids));
//            }else if (searchType==S_ID_SEARCH){
//                commentMapper.selectPage(selectPage,new QueryWrapper<Comment>().eq("aid",awemeId));
//            }else if (searchType==S_IP_SEARCH ){
//                List<Long> ids = commentDetailsMapper.searchByIpLabel(awemeId, searchMsg.toString());
//                commentMapper.selectPage(selectPage,new QueryWrapper<Comment>().eq("aid",awemeId).in("did",ids));
//            }
//            else {
//                commentMapper.selectPage(selectPage,new QueryWrapper<Comment>().eq("aid",awemeId));
//            }
//        }
//
//
//
//        List<Comment> records = selectPage.getRecords();
//        collect = records.stream().map(c -> {
//            Long uid = c.getUid();
//            Long did = c.getDid();
//            CommentDetails commentDetails = commentDetailsMapper.selectById(did);
//            CommentDetailsVo commentDetailsVo=new CommentDetailsVo();
//            BeanUtils.copyProperties(commentDetails,commentDetailsVo);
//            commentDetailsVo.setCreateTime(getFormatDate(commentDetails.getCreateTime()));
//            CommentUser commentUser = commentUserMapper.selectById(uid);
//            CommentVo commentVo = new CommentVo();
//            commentVo.setId(c.getId());
//            commentVo.setCommentUser(commentUser);
//            commentVo.setCommentDetails(commentDetailsVo);
//            commentVo.setUserCardLink(cardUrlPrefix + commentUser.getSecUid());
//            return commentVo;
//        }).collect(Collectors.toList());
//        PageVo pageVo=new PageVo();
//        pageVo.setList(collect);
//        pageVo.setTotals(selectPage.getTotal());
//        pageVo.setTotalPages(selectPage.getPages());
//        pageVo.setInfo(videoInfoVo);
//        return RestData.success(pageVo);
//    }
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
        if (sortType.equals(ORDER_IP) || sortType.equals(ORDER_DATE)){
            String sortField = "";
            if (sortType.equals(ORDER_IP)){
                sortField="ip_label";
            }else if (sortType.equals(ORDER_DATE)){
                sortField="create_time";

            }
            //排序
            if (searchType==S_QUERY_SEARCH ){
                List<Long> ids = commentDetailsMapper.searchByWordSort(awemeId, searchMsg.toString(), sortField,current,pageSize);
                commentDetailsMapper.selectPage(selectPage,new QueryWrapper<CommentDetails>().eq("aweme_id",awemeId).in("id",ids).orderByAsc(sortField));
            }else if (searchType==S_ID_SEARCH){
                commentDetailsMapper.selectPage(selectPage,new QueryWrapper<CommentDetails>().eq("aweme_id",awemeId));
            }else if (searchType==S_IP_SEARCH ){
                List<Long> ids = commentDetailsMapper.searchByIpLabel(awemeId, searchMsg.toString());
                commentDetailsMapper.selectPage(selectPage,new QueryWrapper<CommentDetails>().eq("aweme_id",awemeId).in("id",ids).orderByAsc(sortField));
            }else if (searchType==S_DATE_SEARCH ){
                String startDateTimeStr = searchMsg.toString()+":00";
                String eneDateTimeStr = searchMsg.toString()+":59";
                List<Long> ids = commentDetailsMapper.selectList(new QueryWrapper<CommentDetails>().select("id")
                        .ge("create_time",startDateTimeStr).le("create_time",eneDateTimeStr)).stream().map(cd->cd.getId()).collect(Collectors.toList());
                commentDetailsMapper.selectPage(selectPage,new QueryWrapper<CommentDetails>().eq("aweme_id",awemeId).in("id",ids).orderByAsc(sortField));
            }
            else {
                commentDetailsMapper.selectPage(selectPage,new QueryWrapper<CommentDetails>().eq("aweme_id",awemeId).orderByAsc(sortField));
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
            commentDetailsVo.setCreateTime(getFormatDate(cd.getCreateTime()));
            CommentUser commentUser = commentUserMapper.selectById(c.getUid());
            CommentVo commentVo = new CommentVo();
            commentVo.setId(c.getId());
            commentVo.setCommentUser(commentUser);
            commentVo.setCommentDetails(commentDetailsVo);
            commentVo.setUserCardLink(cardUrlPrefix + commentUser.getSecUid());

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
    @Override
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
    }
    @Autowired
    ThreadService threadService;
    @Autowired
    ConcurrentHashMap<String,Object>stateMap;
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
    }
    @Value("${douyin.cookie}")
    String cookie;

    @Autowired
    ConcurrentHashMap<String,Object>bufMap;
    /**
     * 注意,cookie最好常换，而且要使用登录的账户的cookie
     * @param url
     * @throws IOException
     * @throws ParseException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void harvestComments(String url,HarvestCommentUrl c) throws IOException, ParseException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
//        url=dealUrl(url);
        String aid=getAidFromUrl(url);
//        MediaType mediaType = MediaType.parse("text/plain");
//        RequestBody body = RequestBody.create(mediaType, "");
        String userAgent="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36 Edg/113.0.1774.50";
        String referer=searchLinkPrefix+aid;
//                "https://www.douyin.com/discover?modal_id=7230753691257605409";
        Request request = new Request.Builder()
                .url(url)
                .method("GET",null)
                .addHeader("referer", referer)
                .addHeader("authority", "www.douyin.com")
                .addHeader("cookie",cookie)
                .addHeader("user-agent", userAgent)
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody b = response.body();
        JSONParser jsonParser=new JSONParser(b.string());
        LinkedHashMap<String, Object> stringObjectLinkedHashMap = jsonParser.parseObject();
        int cnt=0;
        for (Map.Entry<String, Object> e : stringObjectLinkedHashMap.entrySet()) {
            if (e.getKey().equals("comments")){
                ArrayList<Map> value = (ArrayList) e.getValue();
                if (null == value){
                    break;
                }
                for (Map map : value) {
                    CommentDetails commentDetails=fillCommentDetails(map);
                    commentDetailsMapper.insertForId(commentDetails);
                    CommentUser commentUser=fillCommentUser(map);
                    commentUserMapper.insertForId(commentUser);
                    Comment comment=new Comment();
                    comment.setAid(commentDetails.getAwemeId());
                    comment.setUid(commentUser.getId());
                    comment.setDid(commentDetails.getId());
                    commentMapper.insert(comment);
                    cnt++;
                }
            }
        }
        threadService.updateUrlStatus(harvestCommentUrlMapper,c);
        bufMap.put(CUR_HARVEST_COMMENTS_CNT,(int)bufMap.getOrDefault(CUR_HARVEST_COMMENTS_CNT,0)+cnt);
    }

    private static String getAidFromUrl(String url) {
        int idx=url.indexOf("&aweme_id=")+10;
        StringBuilder sb=new StringBuilder();
        for (int i = idx; i < url.length(); i++) {
            if (url.charAt(i)=='&'){
                break;
            }
            sb.append(url.charAt(i));
        }
        return sb.toString();
    }

    private String dealUrl(String url) {
        String s = url.replaceFirst("&count=20", "");
        String ns=s.substring(0,s.indexOf("&cursor"))+"&count=20"+s.substring(s.indexOf("&cursor"));
        return ns;
    }

    private static String unicoderString(String str){
        String regex = "\\\\x[0-9a-fA-F]{2}";
        return str.replaceAll(regex, "emoji");
    }
    private CommentUser fillCommentUser(Map map) {
        try {
            Map<String,Object> user = (Map) map.get("user");
            String uid = user.get("uid").toString();
            String sec_uid = user.get("sec_uid").toString();

            Map<String,Object> avatar_168x168 = (Map) user.get("avatar_168x168");
            List url_list = (List) avatar_168x168.get("url_list");
            String avatar = url_list.get(0).toString();

            String region = user.get("region").toString();

            String language = user.get("language").toString();
            String nickname = user.get("nickname").toString();
//            nickname=unicoderString(nickname);
//            log.debug("处理后的nickname {}",nickname);

            String user_age = user.get("user_age").toString();

            CommentUser commentUser=new CommentUser();
            commentUser.setAvatar(avatar).setUserAge(Integer.valueOf(user_age)).setLanguage(language).setNickname(nickname).setSecUid(sec_uid)
                    .setUid(uid).setRegion(region);
            return commentUser;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new CommentUser();
    }

    private CommentDetails fillCommentDetails(Map map) {
        String digg_count = map.get("digg_count").toString();
        String ip_label = map.get("ip_label").toString();

        String reply_comment_total = map.get("reply_comment_total").toString();
        String text = map.get("text").toString();
//        text=unicoderString(text);

        BigInteger create_time0 = (BigInteger) map.get("create_time");
//        long create_times=targetTime(create_time0.longValue());

        String aweme_id = map.get("aweme_id").toString();
        String cid = map.get("cid").toString();
        Boolean is_author_digged = (Boolean) map.get("is_author_digged");
        CommentDetails commentDetails=new CommentDetails();
        commentDetails.setCid(cid).setAwemeId(aweme_id).setIpLabel(ip_label)
                .setCreateTime(new Date(create_time0.longValue()*1000L)).setDiggCount(Integer.valueOf(digg_count))
                .setReplyCommentTotal(Integer.valueOf(reply_comment_total)).setText(text).setIsAuthorDigged(is_author_digged);
        return commentDetails;
    }


    public static long targetTime(long time){
        return time * 1000L;
    }
    public static String getTargetTime(long time){
     try {
         // 将时间戳转换为毫秒
         long timestampInMillis = time * 1000L;

         // 创建Date对象
         Date date = new Date(timestampInMillis);

         // 创建SimpleDateFormat对象，指定日期和时间格式
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

         // 格式化日期和时间，并返回字符串
         return sdf.format(date);
     } catch (Exception e) {
         e.printStackTrace();
         return "";
     }
    }



    public static void main(String[] args) {
        String aidFromUrl = getAidFromUrl("https://www.douyin.com/aweme/v1/web/comment/list/?device_platform=webapp&aid=6383&channel=channel_pc_web&aweme_id=7230753691257605409&cursor=20&count=20&item_type=0&insert_ids=&rcFT=&pc_client_type=1&version_code=170400&version_name=17.4.0&cookie_enabled=true&screen_width=1536&screen_height=864&browser_language=zh-CN&browser_platform=Win32&browser_name=Edge&browser_version=113.0.1774.50&browser_online=true&engine_name=Blink&engine_version=113.0.0.0&os_name=Windows&os_version=10&cpu_core_num=8&device_memory=8&platform=PC&downlink=0.4&effective_type=4g&round_trip_time=100&webid=7209133947333920260&msToken=epwRLsPThG8TTifBULS8i26DiAXNEJHUq7ffbTUKqO2oZaCgRfi38oLPto5XTuhnBSHcvTDO3TyOUgxTg0VT4tNL1wbhOcjIeoCddliPtTAkJzmaVOh9PA==&X-Bogus=DFSzswVLKuTANr7yttPLa5ppgiuT");
        System.out.println(aidFromUrl);
    }
}
