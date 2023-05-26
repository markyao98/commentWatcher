package com.markyao.httpHarvest;

import okhttp3.*;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *  爬取抖音评论:
 *  每一次请求，都会生成一个ms_token，这个token是一直在变化的，甚至连翻页都会变化。
 *  所以只能通过模拟人的刷抖音行为，去获取新的token。
 *  但是，我发现每次获得的数量并不限于20条，而是可以在count这个参数最大这里可以设置60
 *  所以需要解决的问题是: 如何模拟人的行为刷抖音，并且复制请求的url进去呢？
 */
@Component
public class HttpService3 {
    public static void main(String[] args) {
        Date date=new Date(1681966647L);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(date));
    }
    private final static String url1="https://www.douyin.com/aweme/v1/web/comment/list/?device_platform=webapp&aid=6383&channel=channel_pc_web&aweme_id=7227254649298963744&cursor=0&count=20&item_type=0&insert_ids=&rcFT=&pc_client_type=1&version_code=170400&version_name=17.4.0&cookie_enabled=true&screen_width=1536&screen_height=864&browser_language=zh-CN&browser_platform=Win32&browser_name=Edge&browser_version=113.0.1774.35&browser_online=true&engine_name=Blink&engine_version=113.0.0.0&os_name=Windows&os_version=10&cpu_core_num=8&device_memory=8&platform=PC&downlink=10&effective_type=4g&round_trip_time=100&webid=7209133947333920260&msToken=H-LuV--mV0Jw4pDQpAXsLCITY0dWlJ6ZlZGVXQucxBP6KVACJseb4RLTpNux1LCcRBdmfQyf9KYpiRfIvf7g95nik1OKy4f8j0h3W4nGdFNkv46p9xnyLQ==&X-Bogus=DFSzswVEEF0AN91JtCyy4QppgimC";
    private final static String url2="https://www.douyin.com/aweme/v1/web/comment/list/?device_platform=webapp&aid=6383&channel=channel_pc_web&aweme_id=7223815419029720376&cursor=0&count=20&item_type=0&insert_ids=&rcFT=&pc_client_type=1&version_code=170400&version_name=17.4.0&cookie_enabled=true&screen_width=1536&screen_height=864&browser_language=zh-CN&browser_platform=Win32&browser_name=Edge&browser_version=113.0.1774.35&browser_online=true&engine_name=Blink&engine_version=113.0.0.0&os_name=Windows&os_version=10&cpu_core_num=8&device_memory=8&platform=PC&downlink=10&effective_type=4g&round_trip_time=100&webid=7209133947333920260&msToken=RslHlz1vNEU4fj9XlOH9JHFG4z5qzZUuXlaEjifCUYV8J_2Gvg1ihLoRIH-ll47fcmsDnEoQQYXDPTeYKZrAbJLXHOh2cn7wxOTK-PBDZh0-OTIJWi2vdw==&X-Bogus=DFSzswVLLDxANep-tCyrwOppgimS";
    private final static String url="";
    private final static String logurl3="https://www.douyin.com/aweme/v1/web/comment/list/?device_platform=webapp&aid=6383&channel=channel_pc_web&aweme_id=7231004761837833529&cursor=0&count=20&item_type=0&insert_ids=&rcFT=&pc_client_type=1&version_code=170400&version_name=17.4.0&cookie_enabled=true&screen_width=1536&screen_height=864&browser_language=zh-CN&browser_platform=Win32&browser_name=Edge&browser_version=113.0.1774.35&browser_online=true&engine_name=Blink&engine_version=113.0.0.0&os_name=Windows&os_version=10&cpu_core_num=8&device_memory=8&platform=PC&downlink=10&effective_type=4g&round_trip_time=50&webid=7209133947333920260&msToken=v3srg58Ey4eNlirvmk82VpcT1nCD24U2kUKDlXIPEdfQEC57o3FVMCfl7yjzge9sCNsH29oLjRmpr1pYls2P_PM2hi-pteUIyVOXBQQb_8-rIa6wdolIyQ==&X-Bogus=DFSzswVLegxANep-tCyG/5ppgiFT";
    private final static String logurl2="https://www.douyin.com/aweme/v1/web/comment/list/?device_platform=webapp&aid=6383&channel=channel_pc_web&aweme_id=7229006474641427769&cursor=20&count=20&item_type=0&insert_ids=&rcFT=&pc_client_type=1&version_code=170400&version_name=17.4.0&cookie_enabled=true&screen_width=1536&screen_height=864&browser_language=zh-CN&browser_platform=Win32&browser_name=Edge&browser_version=113.0.1774.35&browser_online=true&engine_name=Blink&engine_version=113.0.0.0&os_name=Windows&os_version=10&cpu_core_num=8&device_memory=8&platform=PC&downlink=10&effective_type=4g&round_trip_time=100&webid=7209133947333920260&msToken=ZDvYT6yj77mF6Qrvq4-1HfLpzeOB3qZZSR8uh8xqIRm5i0xDwPIsv07zNeRDzmcJ730brgkyfdcDoljVc16SB6_1zBBMi_WNNrxhIzQcJzWg3xFAT_BCog==&X-Bogus=DFSzswVuAazANSvCtCyex5ppgiFP";
    private final static String lourl1="https://www.douyin.com/aweme/v1/web/comment/list/?device_platform=webapp&aid=6383&channel=channel_pc_web&aweme_id=7228719179543612727&cursor=0&count=20&item_type=0&insert_ids=&rcFT=&pc_client_type=1&version_code=170400&version_name=17.4.0&cookie_enabled=true&screen_width=1536&screen_height=864&browser_language=zh-CN&browser_platform=Win32&browser_name=Edge&browser_version=113.0.1774.35&browser_online=true&engine_name=Blink&engine_version=113.0.0.0&os_name=Windows&os_version=10&cpu_core_num=8&device_memory=8&platform=PC&downlink=10&effective_type=4g&round_trip_time=100&webid=7209133947333920260&msToken=NJz2YEpimTqDVEBIKANnHIaIvqsWw7eSlKuY-YbdgSasrPFE_JFdhxN7EqatUcr_vmyGvsrJKPQ0eGO6D8eNK_GkOCHAPMGB3XOYJsy2pu7xyuJfmXz2jA==&X-Bogus=DFSzswVYBqxANVVYtCye6QppgiFb";
    private final static String url3="https://www.douyin.com/aweme/v1/web/comment/list/?device_platform=webapp&aid=6383&channel=channel_pc_web&aweme_id=7226715777930480931&cursor=40&count=20&item_type=0&insert_ids=&rcFT=&pc_client_type=1&version_code=170400&version_name=17.4.0&cookie_enabled=true&screen_width=1536&screen_height=864&browser_language=zh-CN&browser_platform=Win32&browser_name=Edge&browser_version=113.0.1774.35&browser_online=true&engine_name=Blink&engine_version=113.0.0.0&os_name=Windows&os_version=10&cpu_core_num=8&device_memory=8&platform=PC&downlink=10&effective_type=4g&round_trip_time=100&webid=7209133947333920260&msToken=dZQEOeFDGkL4ZSLLy0Isa2kDmGB9IfDjyjZ4Ufatc-w7GZDQ2pMdlDItcNOwQqvEHAaK1juh1Z_43hOrKM2HwXx2tmy280pBS0ZO7S0CKSrjj013pXGwaQ==&X-Bogus=DFSzswVLj02ANep-tCytGOppgimX";

    public void test() throws IOException, ParseException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(logurl3)
                .method("GET",null)
                .addHeader("cookie", "ttwid=1%7CbP-fRRnONd3tbami5C2-wCs0FPyZXVbh7Q9P7SsWN5I%7C1678507310%7C76f39dbeea2f2d46f7b406cf50fc8571c07682ae5a80d8a56a286bda804f90ea; passport_csrf_token=92b9aebc5018debac3d3380c2d47dd10; passport_csrf_token_default=92b9aebc5018debac3d3380c2d47dd10; s_v_web_id=verify_lf3fxl5k_ttJIQzjK_kWzC_4kAX_9Fni_4ODLCGBtpqIb; n_mh=stKLNvn5jFOcfxueXpvXcsaGmFFrkq9Md67uXioHVVU; toutiao_sso_user=e7fe9896a57d5c0d1d36ea8f37a770c4; toutiao_sso_user_ss=e7fe9896a57d5c0d1d36ea8f37a770c4; store-region=cn-gd; store-region-src=uid; d_ticket=61d254d65d1d15012075ae8638485fc48ed70; my_rd=1; pwa2=%220%7C1%22; publish_badge_show_info=%220%2C0%2C0%2C1683034179989%22; SEARCH_RESULT_LIST_TYPE=%22single%22; FOLLOW_LIVE_POINT_INFO=%22MS4wLjABAAAAdCMshBZWi5t9dwh6jQnhKpcdPvCiaN3-hh_R-wvJpKc%2F1683043200000%2F0%2F0%2F1683036456622%22; douyin.com; strategyABtestKey=%221683601327.564%22; csrf_session_id=219e5b4fa422616fa7df0ea2ae60fc91; bd_ticket_guard_client_data=eyJiZC10aWNrZXQtZ3VhcmQtdmVyc2lvbiI6MiwiYmQtdGlja2V0LWd1YXJkLWl0ZXJhdGlvbi12ZXJzaW9uIjoxLCJiZC10aWNrZXQtZ3VhcmQtY2xpZW50LWNlcnQiOiItLS0tLUJFR0lOIENFUlRJRklDQVRFLS0tLS1cbk1JSUNFekNDQWJxZ0F3SUJBZ0lVU0VKOWlmb0xuY2p1NnBZa0YwdEJraWc4ekxJd0NnWUlLb1pJemowRUF3SXdcbk1URUxNQWtHQTFVRUJoTUNRMDR4SWpBZ0JnTlZCQU1NR1hScFkydGxkRjluZFdGeVpGOWpZVjlsWTJSellWOHlcbk5UWXdIaGNOTWpNd016RXhNRFF3TXpBeVdoY05Nek13TXpFeE1USXdNekF5V2pBbk1Rc3dDUVlEVlFRR0V3SkRcblRqRVlNQllHQTFVRUF3d1BZbVJmZEdsamEyVjBYMmQxWVhKa01Ga3dFd1lIS29aSXpqMENBUVlJS29aSXpqMERcbkFRY0RRZ0FFUExrL2M1UFJDSzdCaU9odVdsbnFGV2ZOU29pWEo2bXI1VnQ2M1NRWTBXL2ZoWmo2eE8zZDFIcExcbldkUkFKZjFnL2ZyV0pSUXlxVGpwOUdvZmkxSTk0Nk9CdVRDQnRqQU9CZ05WSFE4QkFmOEVCQU1DQmFBd01RWURcblZSMGxCQ293S0FZSUt3WUJCUVVIQXdFR0NDc0dBUVVGQndNQ0JnZ3JCZ0VGQlFjREF3WUlLd1lCQlFVSEF3UXdcbktRWURWUjBPQkNJRUlIKzdEb1JVdm1Wa3VUc1JQZUZsRS9zd1Jqd3dGL0lyQVZlL0tBRzRmamVkTUNzR0ExVWRcbkl3UWtNQ0tBSURLbForcU9aRWdTamN4T1RVQjdjeFNiUjIxVGVxVFJnTmQ1bEpkN0lrZURNQmtHQTFVZEVRUVNcbk1CQ0NEbmQzZHk1a2IzVjVhVzR1WTI5dE1Bb0dDQ3FHU000OUJBTUNBMGNBTUVRQ0lCMEpmUmpEYlppbTE3SWFcbml2QXYySnFuaFhJMmIvemlvNWhqaFdYRnQ3MVdBaUFsNmRaakpoa2Nsd0RodmxLY3pkWCtJTGpvT3puOThWWUVcbmVGR1JTdFR5QUE9PVxuLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLVxuIn0=; download_guide=%223%2F20230509%22; FOLLOW_NUMBER_YELLOW_POINT_INFO=%22MS4wLjABAAAAdCMshBZWi5t9dwh6jQnhKpcdPvCiaN3-hh_R-wvJpKc%2F1683648000000%2F0%2F0%2F1683615086221%22; LOGIN_STATUS=0; sso_uid_tt=40569e7be7bf12c1f635484b6f1a56b0; sso_uid_tt_ss=40569e7be7bf12c1f635484b6f1a56b0; sid_ucp_sso_v1=1.0.0-KGE0OGE4MmIxNWNiODEyYjJhZTM0MDFhZmUwOTVjNTQzOWRiMTgxODUKCRDd1OeiBhjvMRoCbHEiIGU3ZmU5ODk2YTU3ZDVjMGQxZDM2ZWE4ZjM3YTc3MGM0; ssid_ucp_sso_v1=1.0.0-KGE0OGE4MmIxNWNiODEyYjJhZTM0MDFhZmUwOTVjNTQzOWRiMTgxODUKCRDd1OeiBhjvMRoCbHEiIGU3ZmU5ODk2YTU3ZDVjMGQxZDM2ZWE4ZjM3YTc3MGM0; odin_tt=33910d33437692931af9d4f6735ae2f50d4c3fc21e97618a33cacd8a49fbdb0d; sid_guard=c253c777446f51bb710a20c99968f578%7C1683614302%7C21600%7CTue%2C+09-May-2023+12%3A38%3A22+GMT; uid_tt=24a10514dece63273b5237a77622ce47; uid_tt_ss=24a10514dece63273b5237a77622ce47; sid_tt=c253c777446f51bb710a20c99968f578; sessionid=c253c777446f51bb710a20c99968f578; sessionid_ss=c253c777446f51bb710a20c99968f578; sid_ucp_v1=1.0.0-KGEyZWQ0ZDA4NTNjNzc5OGM3NWRkMjZiMmYyNTA4OTRlOWQ1ZTRhZjQKCBDe1OeiBhgNGgJobCIgYzI1M2M3Nzc0NDZmNTFiYjcxMGEyMGM5OTk2OGY1Nzg; ssid_ucp_v1=1.0.0-KGEyZWQ0ZDA4NTNjNzc5OGM3NWRkMjZiMmYyNTA4OTRlOWQ1ZTRhZjQKCBDe1OeiBhgNGgJobCIgYzI1M2M3Nzc0NDZmNTFiYjcxMGEyMGM5OTk2OGY1Nzg; __ac_nonce=06459ea5e0006fd93b24b; __ac_signature=_02B4Z6wo00f01TYpmKAAAIDAw1ZyPe0HawE2CZwAACnWLGXSlaFcACHKK3RUbjY6mtPgpPKwwJod81MGkygh.LDWEb1DFaWZG8jfHH.RnYDuLzQr1lES5fSaCDNPkGoEO3DwpbdV9NgFoLLm59; VIDEO_FILTER_MEMO_SELECT=%7B%22expireTime%22%3A1684219103644%2C%22type%22%3A1%7D; home_can_add_dy_2_desktop=%221%22; xgplayer_user_id=583134828980; tt_scid=VN3yVNEuy0zX3caB8RPEa60.-Sh2E5rWq.cv9O3OW8sMGuaMzoKJ.k51KM8o3Ik7c496; msToken=YqHZ_jKeQH4UFlMBR0-OCSVSGUtOZ-xRKtjFvpfJEdY4fIWctuavo1RdwY2N2_ngFvUYvBrbGd5EJMYO4F0FUhDD0zF0lQoGF7qTIuOVzqfp1GTSDb83Jw==; msToken=prMMJ1S0RCzl4qCsE8xwBbfwfrjlnJuvegSUPNIwrjowZSB4oHfPVNkhHSSWJc21Umyey86VOpcsbCcjwIi8yXG_HwYgkajWIoTNmNanebfD4CUj5NJP1A==")
                .addHeader("referer", "https://www.douyin.com/")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36 Edg/113.0.1774.35")
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody b = response.body();
//        System.out.println(b);
//        System.out.println(b.string());
        JSONParser jsonParser=new JSONParser(b.string());
//        System.out.println("响应数据长度: "+b.string().length());
        LinkedHashMap<String, Object> stringObjectLinkedHashMap = jsonParser.parseObject();
        for (Map.Entry<String, Object> e : stringObjectLinkedHashMap.entrySet()) {
            if (e.getKey().equals("comments")){
                ArrayList<Map> value = (ArrayList) e.getValue();
                if (null == value){
                    break;
                }
                for (Map map : value) {
                    String digg_count = map.get("digg_count").toString();
                    String reply_comment_total = map.get("reply_comment_total").toString();
                    String text = map.get("text").toString();
                    String ip_label = map.get("ip_label").toString();
                    String create_time = map.get("create_time").toString();
                    String aweme_id = map.get("aweme_id").toString();
                    String cid = map.get("cid").toString();
//                    Map reply_comment = (Map) map.get("reply_comment");
//                    String text = reply_comment.get("text").toString();
                    System.out.println("========================================");
                    System.out.println("ip:"+ip_label+", 创建时间: "+create_time+"  ,评论内容: "+text+"  点赞数:"+digg_count+" 回复量:"+reply_comment_total);
                    System.out.println("作者信息:  aweme_id:"+aweme_id+"  cid:"+cid);
                    System.out.println("========================================");

                }
            }
        }

    }
}
