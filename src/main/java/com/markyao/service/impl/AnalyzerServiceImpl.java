package com.markyao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.markyao.common.Iplist;
import com.markyao.mapper.*;
import com.markyao.model.pojo.*;
import com.markyao.model.vo.CommentDetailsVo;
import com.markyao.model.vo.CommentUserVo;
import com.markyao.model.vo.UserVo;
import com.markyao.service.AnalyzerService;
import com.markyao.service.harvest.CommentService;
import com.markyao.utils.FileUtils;
import com.markyao.utils.SnowflakeIdGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyzerServiceImpl implements AnalyzerService {
    @Autowired
    CommentDetailsMapper commentDetailsMapper;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    VideoInfoMapper videoInfoMapper;
    @Autowired
    SnowflakeIdGenerator idGenerator;
    @Autowired
    CommentService commentService;
    private final static String staticPath=FileUtils.class.getClassLoader().getResource("static").getPath()+"/"+"imgs/";
    private final static String imgPathPrefix="./imgs/";
    @Override
    public String getTextByAid(String aid) {
        List<CommentDetails> commentDetails = commentDetailsMapper.selectList(new QueryWrapper<CommentDetails>().eq("aweme_id", aid).select("text"));
        StringBuilder sb=new StringBuilder();
        commentDetails.stream().map(c->c.getText()).forEach(s->sb.append(s+" "));

        return sb.toString();
    }

    private Map<String,Object>getByAnalyzer(String aid){
        String text = getTextByAid(aid);
        String url = "http://localhost:5000/analysisData";
        Map<String,Object>request=new HashMap<>();
        request.put("text",text);
        MultiValueMap<String,Object>headers=new LinkedMultiValueMap<>();
        headers.add("Content-Type","application/json");
        HttpEntity entity=new HttpEntity(request,headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        Map<String,Object> map = response.getBody();
        String wordCloudUrl=map.get("wordCloudUrl").toString();
        List<List> top_words = (List<List>) map.get("top_words");
        List<String> collect = top_words.stream().map(l -> l.get(0) + " : " + l.get(1)).collect(Collectors.toList());
        StringBuilder sb=new StringBuilder();
        collect.stream().forEach(s->sb.append(s+","));
        long l = idGenerator.generateId();
        String fileName=l+".png";

        Map<String,Object>m=new HashMap<>();
        m.put("fileName",fileName);
        m.put("topWords",collect);
        m.put("wordCloudUrl",wordCloudUrl);
        return m;
    }
    private Map<String,Object>getByAnalyzer(String []aids){
        StringBuilder textsb=new StringBuilder();
        for (String aid : aids) {
            String text = getTextByAid(aid);
            textsb.append("\n"+text);
        }

        String url = "http://localhost:5000/analysisData";
        Map<String,Object>request=new HashMap<>();
        request.put("text",textsb.toString());
        MultiValueMap<String,Object>headers=new LinkedMultiValueMap<>();
        headers.add("Content-Type","application/json");
        HttpEntity entity=new HttpEntity(request,headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        Map<String,Object> map = response.getBody();
        String wordCloudUrl=map.get("wordCloudUrl").toString();
        List<List> top_words = (List<List>) map.get("top_words");
        List<String> collect = top_words.stream().map(l -> l.get(0) + " : " + l.get(1)).collect(Collectors.toList());
        StringBuilder sb=new StringBuilder();
        collect.stream().forEach(s->sb.append(s+","));
        long l = idGenerator.generateId();
        String fileName=l+".png";

        Map<String,Object>m=new HashMap<>();
        m.put("fileName",fileName);
        m.put("topWords",collect);
        m.put("wordCloudUrl",wordCloudUrl);
        return m;
    }

    @Override
    public Map<String, Object> analyzer(String ... aids) {
        if (aids.length==1){
            return singleAnalyzer(aids[0]);
        }
        return multipleAnalyzer(aids);

    }
    @Autowired
    BufWordCloudMapper bufWordCloudMapper;
    private Map<String, Object> multipleAnalyzer(String[] aids) {
        List<VideoInfo> videoInfos = videoInfoMapper.selectList(new QueryWrapper<VideoInfo>().in("aweme_id", aids));

        List<String> collect=null;
        String fileName=null;
        Map<String,Object>result=new HashMap<>();
        DecimalFormat df = new DecimalFormat("#.##%");
        long totals=0;
        String wordCloudUrl=null;
        StringBuilder sb2=new StringBuilder();
        Arrays.stream(aids).forEach(a->sb2.append(a+","));
        String aidss = sb2.deleteCharAt(sb2.length() - 1).toString();
        BufWordCloud bufWordCloud = bufWordCloudMapper.selectOne(new QueryWrapper<BufWordCloud>().eq("aids", aidss));
        if (bufWordCloud!=null){
            fileName=bufWordCloud.getWordCloud();
//            String topWords = bufWordCloud.getTopWords();
            if (bufWordCloud.getTotals()==null || bufWordCloud.getTotals()==0){
                long ftotals= videoInfos.stream().mapToLong(VideoInfo::getTotals).sum();
                bufWordCloud.setTotals(Math.toIntExact(ftotals));
                bufWordCloudMapper.updateById(bufWordCloud);
            }
            totals = bufWordCloud.getTotals();
            collect= Arrays.stream(bufWordCloud.getTopWords().split(",")).collect(Collectors.toList());

            /*if (totals!=null &&totals>0){
                collect=collect.stream().map(s->{
                    int cnt = Integer.parseInt(s.split(":")[1].trim());
                    return s.split(":")[0]+" : "+s.split(":")[1]+" 【"+df.format(cnt*1.0 / totals)+"】";
                }).collect(Collectors.toList());
            }else {
                collect= Arrays.stream(bufWordCloud.getTopWords().split(",")).collect(Collectors.toList());
            }*/
            result.put("wordCloudUrl",fileName);
            result.put("topWords",collect);
        }else {
            bufWordCloud=new BufWordCloud();
            Map<String, Object> byAnalyzer = getByAnalyzer(aids);
            fileName = byAnalyzer.get("fileName").toString();
            wordCloudUrl=byAnalyzer.get("wordCloudUrl").toString();
            collect = (List<String>) byAnalyzer.get("topWords");

            StringBuilder sb=new StringBuilder();
            collect.stream().forEach(s->sb.append(s+","));
            boolean f=false;
            try {
                f=FileUtils.copyImageFile(wordCloudUrl,staticPath+fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (f){
                long  ftotals= videoInfos.stream().mapToLong(VideoInfo::getTotals).sum();
                totals=ftotals;
                if (totals>0){
                    collect=collect.stream().map(s->{
                        int cnt = Integer.parseInt(s.split(":")[1].trim());
                        return s.split(":")[0]+" : "+s.split(":")[1]+" 【"+df.format(cnt*1.0 / ftotals)+"】";
                    }).collect(Collectors.toList());
                }
                bufWordCloud.setWordCloud(fileName);
                StringBuilder sb3=new StringBuilder();
                collect.stream().forEach(s->sb3.append(s+","));
                bufWordCloud.setAids(aidss);
                bufWordCloud.setTopWords(sb3.toString());
                bufWordCloud.setTotals(Math.toIntExact(totals));
                bufWordCloudMapper.insert(bufWordCloud);

                result.put("wordCloudUrl",fileName);
                result.put("topWords",collect);
        }

        }

        //分析ip
        Map<String, Object> ipResult = analyzeripModelList(aids);
        //分析评论日期
        List<Map> dateResult = analyzerDatetime(aids);

        result.put("ipResult",ipResult);
        result.put("dateResult",dateResult);

        //分析同一用户出现的情况
        Map<String, Object> usersResult=analyzerUsers(aids);
        result.put("usersResult",usersResult);
        result.put("totals",totals);
        return result;
    }
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    CommentUserMapper commentUserMapper;

    /**
     * @Description 分析多个视频底下出现同个用户的情况
     * @Author markyao
     * @Date  2023/5/24
     */
    private Map<String, Object> analyzerUsers(String[] aids) {
        List<Comment> c1 = new ArrayList<>();
        for (String aid : aids) {
            c1.addAll(commentMapper.selectList(new QueryWrapper<Comment>().eq("aid", aid)));
        }

        List<Long> uids1 = c1.stream().map(c -> c.getUid()).collect(Collectors.toList());
        List<CommentUser> users1 = commentUserMapper.selectList(new QueryWrapper<CommentUser>().in("id", uids1));


        Map<String, Set<String>>mp=new HashMap<>();

        for (CommentUser u1 : users1) {
            String sid1 = u1.getSecUid();
            if (sid1==null){
                continue;
            }
            sid1=sid1.trim();
            Set<String> l1 = mp.getOrDefault(sid1, new HashSet<>());
            UserVo userVo1 = new UserVo();
            userVo1.setId(u1.getId()).setSecUid(u1.getSecUid());
            l1.add(u1.getId()+"");
            mp.put(sid1,l1);
        }
        List<CommentUserVo>result0 =new ArrayList<>();
        Map<String,List<CommentUserVo>>result1=new HashMap<>();
        //key为secUid,值为uidList
        for (Map.Entry<String, Set<String>> entry : mp.entrySet()) {
            Set<String> list = entry.getValue();
            if (list.size()>1) {
                list=deduplicationSet(list);
                if (list.size()>1){
                    CommentUserVo commentUserVo=new CommentUserVo();
                    CommentUser commentUser =null;
                    List<CommentDetailsVo>list1=new ArrayList<>();
                    for (String uid : list) {
                        if (commentUser==null){
                            commentUser=commentUserMapper.selectById(uid);
                            commentUserVo.setUser(commentUser);
                        }
                        Comment comment = commentMapper.selectOne(new QueryWrapper<Comment>().eq("uid", uid).select("did"));
                        CommentDetails commentDetails = commentDetailsMapper.selectOne(new QueryWrapper<CommentDetails>().eq("id", comment.getDid()));
                        CommentDetailsVo commentDetailsVo=new CommentDetailsVo();
                        BeanUtils.copyProperties(commentDetails,commentDetailsVo);
                        commentDetailsVo.setCreateTime(getFormatDate(commentDetails.getCreateTime()));
                        VideoInfo videoInfo = videoInfoMapper.selectOne(new QueryWrapper<VideoInfo>().eq("aweme_id", commentDetails.getAwemeId()).select("title_info"));
                        commentDetailsVo.setVideoTitle(videoInfo.getTitleInfo());
                        list1.add(commentDetailsVo);
                    }
                    commentUserVo.setCommentDetailList(list1);
                    result0.add(commentUserVo);
                    List<CommentUserVo> l2 = result1.getOrDefault(list1.size() + "", new ArrayList<>());
                    l2.add(commentUserVo);
                    result1.put(list1.size()+"",l2);
                }
            }
        }
        Map<String,Object>result=new HashMap<>();
//        result.put("list",result0);
        result.put("map",result1);
        return result;
    }
    private Set<String> deduplicationSet(Set<String> list) {
        Set<String>set=new HashSet<>();
        Set<String>result=new HashSet<>();
        for (String uid : list) {
            Comment comment = commentMapper.selectOne(new QueryWrapper<Comment>().eq("uid", uid).select("did"));
            CommentDetails commentDetails = commentDetailsMapper.selectOne(new QueryWrapper<CommentDetails>().eq("id", comment.getDid()).select("text","aweme_id"));
            String text = commentDetails.getText();
            String aid = commentDetails.getAwemeId();
            String key=text.trim()+aid;
            if (!set.contains(key)){
                result.add(uid);
                set.add(key);
            }
        }
        return result;
    }
    /**
     * 单视频分析
     * @param aid
     * @return
     */
    private Map<String, Object> singleAnalyzer(String aid) {
        VideoInfo videoInfo = videoInfoMapper.selectOne(new QueryWrapper<VideoInfo>().eq("aweme_id",aid));
        List<String> collect=null;
        String fileName=null;
        Map<String,Object>result=new HashMap<>();
        DecimalFormat df = new DecimalFormat("#.##%");

        if (StringUtils.hasText(videoInfo.getWordCloud()) && StringUtils.hasText(videoInfo.getTopWords())){
            fileName = videoInfo.getWordCloud();
            collect = Arrays.stream(videoInfo.getTopWords().split(",")).collect(Collectors.toList());
            result.put("wordCloudUrl",fileName);
            if (videoInfo.getTotals()!=null && videoInfo.getTotals()>0){
                collect=collect.stream().map(s->{
                    int cnt = Integer.parseInt(s.split(":")[1].trim());
                    return s.split(":")[0]+" : "+s.split(":")[1]+" 【"+df.format(cnt*1.0 / videoInfo.getTotals())+"】";
                }).collect(Collectors.toList());
            }

            result.put("topWords",collect);
        }

        else {
            String wordCloudUrl=null;
            Map<String, Object> byAnalyzer = getByAnalyzer(aid);
            fileName = byAnalyzer.get("fileName").toString();
            wordCloudUrl=byAnalyzer.get("wordCloudUrl").toString();
            collect = (List<String>) byAnalyzer.get("topWords");
            videoInfo.setWordCloud(fileName);

            StringBuilder sb=new StringBuilder();
            collect.stream().forEach(s->sb.append(s+","));
            videoInfo.setTopWords(sb.toString());

            videoInfoMapper.updateById(videoInfo);
            boolean f=false;
            try {
                f=FileUtils.copyImageFile(wordCloudUrl,staticPath+fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (f){
                if (videoInfo.getTotals()!=null && videoInfo.getTotals()>0){
                    collect=collect.stream().map(s->{
                        int cnt = Integer.parseInt(s.split(":")[1].trim());
                        return s.split(":")[0]+" : "+s.split(":")[1]+" 【"+df.format(cnt*1.0 / videoInfo.getTotals())+"】";
                    }).collect(Collectors.toList());
                }
                result.put("wordCloudUrl",fileName);
                result.put("topWords",collect);
            }else {
                result.put("wordCloudUrl","xx.png");
                result.put("topWords",null);

            }
        }

        //分析ip
        Map<String, Object> ipResult = analyzeripModelList(aid);
        //分析评论日期
        List<Map> dateResult = analyzerDatetime(aid);

        result.put("ipResult",ipResult);
        result.put("dateResult",dateResult);


        //todo 分析情绪
        return result;
    }

    /**
     * 分析ip的情况
     * @param aid
     * @return
     */
    @Override
    public Map<String, Object> analyzeripModelList(String aid) {
        int total = commentDetailsMapper.selectCount(new QueryWrapper<CommentDetails>().eq("aweme_id",aid));

        //ip出现次数排名
        List<IpModel> ipModels = commentDetailsMapper.selectIpModelDesc(aid);
        DecimalFormat df = new DecimalFormat("#.##%");

        //非大陆地区的ip
        List<IpModel> noChinaContinents = ipModels.stream().filter(ip-> !Iplist.CHAINA_IP_LABELS.contains(ip.getIpLabel())).map(ip->{
            ip.setDiv(df.format(ip.getCnt() * 1.0 / total));
            return ip;
        }).collect(Collectors.toList());
        List<IpModel> chainaContinents= ipModels.stream().filter(ip-> Iplist.CHAINA_IP_LABELS.contains(ip.getIpLabel())).map(ip->{
            ip.setDiv(df.format(ip.getCnt() * 1.0 / total));
            return ip;
        }).collect(Collectors.toList());
        Map<String,Object>result=new HashMap<>();
        result.put("ipModels",chainaContinents);
        result.put("noChinas",noChinaContinents);


        return result;
    }
    public Map<String, Object> analyzeripModelList(String []aids) {
        long total = 0;

        List<IpModel> ipModels=commentDetailsMapper.selectIpModelDescByAids(aids);
        //ip出现次数排名
        for (String aid : aids) {
            total+=commentDetailsMapper.selectCount(new QueryWrapper<CommentDetails>().eq("aweme_id",aid));
        }

        DecimalFormat df = new DecimalFormat("#.##%");
        //非大陆地区的ip
        long finalTotal = total;
        List<IpModel> noChinaContinents = ipModels.stream().filter(ip-> !Iplist.CHAINA_IP_LABELS.contains(ip.getIpLabel())).map(ip->{
            ip.setDiv(df.format(ip.getCnt() * 1.0 / finalTotal));
            return ip;
        }).collect(Collectors.toList());
        List<IpModel> chainaContinents= ipModels.stream().filter(ip-> Iplist.CHAINA_IP_LABELS.contains(ip.getIpLabel())).map(ip->{
            ip.setDiv(df.format(ip.getCnt() * 1.0 / finalTotal));
            return ip;
        }).collect(Collectors.toList());
        Map<String,Object>result=new HashMap<>();
        result.put("ipModels",chainaContinents);
        result.put("noChinas",noChinaContinents);


        return result;
    }

    /**
     * 分析评论时间是否有高度重合的
     * @param aid
     * @return
     */
    @Override
    public List<Map> analyzerDatetime(String aid) {
        Map<String,List<Long>>dateMap=new HashMap<>();
        TreeMap<String, Integer> tmap = new TreeMap<>();


        List<CommentDetails> commentDetails = commentDetailsMapper.selectList(new QueryWrapper<CommentDetails>().eq("aweme_id", aid).select("id", "create_time"));
        for (CommentDetails commentDetail : commentDetails) {
            Date createTime = commentDetail.getCreateTime();

            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String datetime = sdf.format(createTime);
            List<Long> l =dateMap.getOrDefault(datetime.toString(),new ArrayList<>());
            l.add(commentDetail.getId());
            dateMap.put(datetime, l);
            tmap.put(datetime,l.size());
        }
        //排序
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(tmap.entrySet());
        Collections.sort(sortedEntries, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                return entry2.getValue().compareTo(entry1.getValue());
            }
        });
        List<Map>result=new ArrayList<>();
        //获取前20个频率最高的评论时间
        for (int i = 0; i < 20; i++) {
            Map<String,Object>map=new HashMap<>();
            Map.Entry<String, Integer> entry = sortedEntries.get(i);
            List<Long> ids = dateMap.get(entry.getKey());
            List<CommentDetails> commentDetails1 = commentDetailsMapper.selectList(new QueryWrapper<CommentDetails>().in("id", ids));
//            result.put(entry.getKey(),commentDetails1);
            List<CommentDetailsVo>commentDetailsVos=new ArrayList<>();
            commentDetails1.forEach(c->commentDetailsVos.add(getDetailsVo(c)));
            map.put(entry.getKey(),commentDetailsVos);
            result.add(map);
        }

        return result;
    }
    public List<Map> analyzerDatetime(String []aids) {
        Map<String,List<Long>>dateMap=new HashMap<>();
        TreeMap<String, Integer> tmap = new TreeMap<>();


        List<CommentDetails> commentDetails = new ArrayList<>();
        for (String aid : aids) {
            commentDetails.addAll(commentDetailsMapper.selectList(new QueryWrapper<CommentDetails>().eq("aweme_id", aid).select("id", "create_time")));
        }

        for (CommentDetails commentDetail : commentDetails) {
            Date createTime = commentDetail.getCreateTime();

            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String datetime = sdf.format(createTime);
            List<Long> l =dateMap.getOrDefault(datetime.toString(),new ArrayList<>());
            l.add(commentDetail.getId());
            dateMap.put(datetime, l);
            tmap.put(datetime,l.size());
        }
        //排序
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(tmap.entrySet());
        Collections.sort(sortedEntries, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                return entry2.getValue().compareTo(entry1.getValue());
            }
        });
        List<Map>result=new ArrayList<>();
        //获取前20个频率最高的评论时间
        for (int i = 0; i < 20; i++) {
            Map<String,Object>map=new HashMap<>();
            Map.Entry<String, Integer> entry = sortedEntries.get(i);
            List<Long> ids = dateMap.get(entry.getKey());
            List<CommentDetails> commentDetails1 = commentDetailsMapper.selectList(new QueryWrapper<CommentDetails>().in("id", ids));
//            result.put(entry.getKey(),commentDetails1);
            List<CommentDetailsVo>commentDetailsVos=new ArrayList<>();
            commentDetails1.forEach(c->commentDetailsVos.add(getDetailsVo(c)));
            map.put(entry.getKey(),commentDetailsVos);
            result.add(map);
        }

        return result;
    }



    private CommentDetailsVo getDetailsVo(CommentDetails c) {
        CommentDetailsVo commentDetailsVo=new CommentDetailsVo();
        BeanUtils.copyProperties(c,commentDetailsVo);
        commentDetailsVo.setCreateTime(getFormatDate(c.getCreateTime()));
        return commentDetailsVo;
    }

    private String getFormatDate(Date createTime) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sdf.format(createTime);
    }


    public static Date getDateFromStr(String str) throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.parse(str);
    }

    public static void main(String[] args) throws ParseException {
        Date createTime=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dstr="2023-05-22 12:32:00";
        Date parse = sdf.parse(dstr);
        System.out.println(parse.getTime());
        System.out.println(parse.getTime()+1000*60);
    }

}
