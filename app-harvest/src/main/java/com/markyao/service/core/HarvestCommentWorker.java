package com.markyao.service.core;

import com.markyao.model.pojo.VideoInfo;
import com.markyao.service.HarvestCommentService;

import com.markyao.service.MonitorDigCountService;
import com.markyao.service.VideoInfoService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import static com.markyao.service.impl.RequestURLServiceImpl.STOP_HARVEST_URL;


@Component
@Slf4j
public class HarvestCommentWorker {
    @Value("${douyin.cookie}")
    String cookie;
    @Value("${douyin.video.searchLinkPrefix}")
    String searchLinkPrefix;
    @Value("${douyin.comment.get}")
    String commentGet;
    @Autowired
    ConcurrentHashMap<String,Object> stateMap;
    @Autowired
    HarvestCommentService harvestCommentService;
    @Autowired
    MonitorDigCountService monitorDigCountService;
    @Autowired
    VideoInfoService videoInfoService;

    private final static int maxSize=50; //dy接口一次最多可请求这么多
    public LinkedHashMap<String, Object> getCommentsResponse(String url) throws IOException, ParseException {
        int uaidx=0;
        int reidx=0;
        String userAgent1="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36 Edg/113.0.1774.50";
        String userAgent2="Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36";
//        String uas[]={userAgent1,userAgent2};
        String uas[]={userAgent2,userAgent1};

        String aid=getAidFromUrl(url);
        String referer1="https://www.douyin.com/";
        String referer2=searchLinkPrefix+aid;
//        String refs[]={referer1,referer2};
        String refs[]={referer2,referer1};
        LinkedHashMap<String, Object> stringObjectLinkedHashMap = null;
/*
当前refer: https://www.douyin.com/discover?modal_id=7240413220765257016,
当前ua :Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36
 */
        while (true) {
            try {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                String refer=refs[reidx];
                String ua=uas[uaidx];
                String ck=cookie;
                Request request = new Request.Builder()
                        .url(url)
                        .method("GET", null)
                        .addHeader("referer", refs[reidx])
                        .addHeader("authority", "www.douyin.com")
                        .addHeader("cookie", cookie)
                        .addHeader("user-agent", uas[uaidx])
                        .build();
                Response response = client.newCall(request).execute();
                stringObjectLinkedHashMap = getStringObjectLinkedHashMap(response);
                log.info("---------------->>>>>>>>>>>>");
                log.info("抓取成功!! 当前cookie(前10位): {},当前refer: {},当前ua :{}",ck.substring(0,10),refer,ua);
                log.info("---------------->>>>>>>>>>>>");
                return stringObjectLinkedHashMap;
            } catch (IOException | ParseException e) {
                e.printStackTrace();
                log.error("抓取数据失败~ 正在更换refer / ua / cookie 进行重试...");
                if (reidx<refs.length-1){
                    reidx++;
                }
                else if (reidx==refs.length-1 && uaidx<uas.length-1){
                    uaidx++;
                }
                else if (reidx==refs.length-1 && uaidx==uas.length-1){
                    log.error("抓取失败~!!! 更换透了都没用~~");
                    break;
                }
                sleep(3);
            }
        }

        return stringObjectLinkedHashMap;
    }
    private final static String postfix=
            "&device_platform=webapp&aid=6383&channel=channel_pc_web&item_type=0&insert_ids=&rcFT=&pc_client_type=1&version_code=170400" +
                    "&version_name=17.4.0&cookie_enabled=true&screen_width=1536&screen_height=864&browser_language=zh-CN&browser_platform=Win32" +
                    "&browser_name=Chrome&browser_version=92.0.4515.107&browser_online=true&engine_name=Blink&engine_version=92.0.4515.107&os_name=Windows&os_version=10" +
                    "&cpu_core_num=8&device_memory=8&platform=PC&downlink=1.45&effective_type=3g&round_trip_time=500&webid=7238907622254134796";
    /**
     * 获取评论列表,一次获取50条
     * @param awemeId 视频Aid
     * @param cur  当前页数
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public LinkedHashMap<String, Object> getCommentsResponseForAid(String awemeId,int cur) throws IOException, ParseException {
        int uaidx=0;
        int reidx=0;
        String userAgent1="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36 Edg/113.0.1774.50";
        String userAgent2="Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36";
        String uas[]={userAgent1,userAgent2};

        String referer1="https://www.douyin.com/";
        String referer2=searchLinkPrefix+awemeId;
        String refs[]={referer1,referer2};

        String url=commentGet+awemeId+"&cursor="+cur+"&count="+maxSize+postfix;
        LinkedHashMap<String, Object> stringObjectLinkedHashMap = null;

        while (true) {
            try {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                String refer=refs[reidx];
                String ua=uas[uaidx];
                String ck=cookie;
                Request request = new Request.Builder()
                        .url(url)
                        .method("GET", null)
                        .addHeader("referer", refs[reidx])
                        .addHeader("authority", "www.douyin.com")
                        .addHeader("cookie", cookie)
                        .addHeader("user-agent", uas[uaidx])
                        .build();
                Response response = client.newCall(request).execute();
                stringObjectLinkedHashMap = getStringObjectLinkedHashMap(response);
                log.info("---------------->>>>>>>>>>>>");
                log.info("抓取成功!! 当前cookie(前10位): {},当前refer: {},当前ua :{}",ck.substring(0,10),refer,ua);
                log.info("---------------->>>>>>>>>>>>");
                return stringObjectLinkedHashMap;
            } catch (IOException | ParseException e) {
                e.printStackTrace();
                log.error("抓取数据失败~ 正在更换refer / ua / cookie 进行重试...");
                if (reidx<refs.length-1){
                    reidx++;
                }
                else if (reidx==refs.length-1 && uaidx<uas.length-1){
                    uaidx++;
                }
                else if (reidx==refs.length-1 && uaidx==uas.length-1){
                    log.error("抓取失败~!!! 更换透了都没用~~");
                    throw e;
                }
                sleep(3);
            }
        }
    }




    private void sleep(int sec){
        try {
            Thread.sleep(sec*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static LinkedHashMap<String, Object> getStringObjectLinkedHashMap(Response response) throws IOException, ParseException {
        ResponseBody b = response.body();
        JSONParser jsonParser=new JSONParser(b.string());
        LinkedHashMap<String, Object> stringObjectLinkedHashMap = jsonParser.parseObject();
        return stringObjectLinkedHashMap;
    }
  public static LinkedHashMap<String, Object> getStringObjectLinkedHashMap(String response) throws IOException, ParseException {
        JSONParser jsonParser=new JSONParser(response);
        LinkedHashMap<String, Object> stringObjectLinkedHashMap = jsonParser.parseObject();
        return stringObjectLinkedHashMap;
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




    /**
     * 通过aid 自动爬取数据
     * @param aid
     */
    public void start0(String aid,String title) throws IOException, ParseException {
        int cur=0;
        while (!stateMap.containsKey(STOP_HARVEST_URL)){
            LinkedHashMap<String, Object> response = getCommentsResponseForAid(aid, cur);
            BigInteger has_more = (BigInteger) response.get("has_more");
            BigInteger cursor = (BigInteger) response.get("cursor");
            cur= cursor.intValue();
            response.put("thisCur",cur);
            response.put("videoTitle",title);
            harvestCommentService.harvestComments(response);

            if (has_more.intValue()==0){
                log.info("aid: {}评论数据已收集完毕~",aid);
                break;
            }else {
                log.info("休息2s~");
                sleep(2);
            }
        }
    }

    /**
     * 通过aid 自动爬取数据,固定cur页码
     * @param aid
     */
    public void start00(String aid,int cur) throws IOException, ParseException {
        monitorDigCountService.monitorForDigCount3(aid,cur,30);

    }
}
