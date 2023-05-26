package com.markyao.httpHarvest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class HttpService {

    private static final String url="https://www.douyin.com/aweme/v1/web/comment/list/?device_platform=webapp&aid=6383&channel=channel_pc_web&aweme_id=7227254649298963744&cursor=0&count=20&item_type=0&insert_ids=&rcFT=&pc_client_type=1&version_code=170400&version_name=17.4.0&cookie_enabled=true&screen_width=1536&screen_height=864&browser_language=zh-CN&browser_platform=Win32&browser_name=Edge&browser_version=113.0.1774.35&browser_online=true&engine_name=Blink&engine_version=113.0.0.0&os_name=Windows&os_version=10&cpu_core_num=8&device_memory=8&platform=PC&downlink=10&effective_type=4g&round_trip_time=100&webid=7209133947333920260&msToken=H-LuV--mV0Jw4pDQpAXsLCITY0dWlJ6ZlZGVXQucxBP6KVACJseb4RLTpNux1LCcRBdmfQyf9KYpiRfIvf7g95nik1OKy4f8j0h3W4nGdFNkv46p9xnyLQ==&X-Bogus=DFSzswVEEF0AN91JtCyy4QppgimC";

    @Autowired
    RestTemplate restTemplate;


    public void test1(){
        HttpHeaders httpHeaders=new HttpHeaders();
        MultiValueMap<String,String>headerMap=new LinkedMultiValueMap<>();
//        headerMap.add("Referer","https://www.douyin.com/");
//        headerMap.add("Authority","www.douyin.com");
        headerMap.add("Cookie","ttwid=1%7CbP-fRRnONd3tbami5C2-wCs0FPyZXVbh7Q9P7SsWN5I%7C1678507310%7C76f39dbeea2f2d46f7b406cf50fc8571c07682ae5a80d8a56a286bda804f90ea; passport_csrf_token=92b9aebc5018debac3d3380c2d47dd10; passport_csrf_token_default=92b9aebc5018debac3d3380c2d47dd10; s_v_web_id=verify_lf3fxl5k_ttJIQzjK_kWzC_4kAX_9Fni_4ODLCGBtpqIb; n_mh=stKLNvn5jFOcfxueXpvXcsaGmFFrkq9Md67uXioHVVU; sso_uid_tt=cb8cabcf818ef33ef07162ee03aef583; sso_uid_tt_ss=cb8cabcf818ef33ef07162ee03aef583; toutiao_sso_user=e7fe9896a57d5c0d1d36ea8f37a770c4; toutiao_sso_user_ss=e7fe9896a57d5c0d1d36ea8f37a770c4; passport_assist_user=CjxVO11OWsxVeAsNc5QHrOBD3UJpe8rBdRFnFMSu99AjijWti2sxCq5Js7z4gOTJQFzcRAsxva6AbRWWLEUaSAo8vPpElwouhMLy2s01-IIOS3IoXyX2QG2dqrrELEhon7Ve7b5w7XIjqvSFavayHJXmLpKbit_AmCetWl5iEK26qw0Yia_WVCIBAwouk8M%3D; odin_tt=a150c64ccfbd33e9676d8f4db473a3581498a7ba33e9e0d46f333f40fc0cee04139ddc3a4f6f12371e36d53928397716; uid_tt=f6f5fb96cce1b2210cf57b03a883c17f; uid_tt_ss=f6f5fb96cce1b2210cf57b03a883c17f; sid_tt=d69d38ef32bdd1380473ad6756cdbcee; sessionid=d69d38ef32bdd1380473ad6756cdbcee; sessionid_ss=d69d38ef32bdd1380473ad6756cdbcee; LOGIN_STATUS=1; store-region=cn-gd; store-region-src=uid; d_ticket=61d254d65d1d15012075ae8638485fc48ed70; my_rd=1; pwa2=%220%7C1%22; publish_badge_show_info=%220%2C0%2C0%2C1683034179989%22; sid_ucp_sso_v1=1.0.0-KDdjN2QyMzRmNDIxODIwNTE4NWJkYTI5YjkxODUyZDBjNWFlMzk5Y2IKHQiI5cbq3wIQxKDEogYY7zEgDDDy9OrUBTgGQPQHGgJsZiIgZTdmZTk4OTZhNTdkNWMwZDFkMzZlYThmMzdhNzcwYzQ; ssid_ucp_sso_v1=1.0.0-KDdjN2QyMzRmNDIxODIwNTE4NWJkYTI5YjkxODUyZDBjNWFlMzk5Y2IKHQiI5cbq3wIQxKDEogYY7zEgDDDy9OrUBTgGQPQHGgJsZiIgZTdmZTk4OTZhNTdkNWMwZDFkMzZlYThmMzdhNzcwYzQ; sid_guard=d69d38ef32bdd1380473ad6756cdbcee%7C1683034181%7C5184000%7CSat%2C+01-Jul-2023+13%3A29%3A41+GMT; sid_ucp_v1=1.0.0-KGE5YzY2OTE1ZWZmNGRkMzg4MWFmYjBhMTQxYjljMTU4ZDE5YzllNDkKGQiI5cbq3wIQxaDEogYY7zEgDDgGQPQHSAQaAmxxIiBkNjlkMzhlZjMyYmRkMTM4MDQ3M2FkNjc1NmNkYmNlZQ; ssid_ucp_v1=1.0.0-KGE5YzY2OTE1ZWZmNGRkMzg4MWFmYjBhMTQxYjljMTU4ZDE5YzllNDkKGQiI5cbq3wIQxaDEogYY7zEgDDgGQPQHSAQaAmxxIiBkNjlkMzhlZjMyYmRkMTM4MDQ3M2FkNjc1NmNkYmNlZQ; __ac_signature=_02B4Z6wo00f01.gji.QAAIDCDVxhady2x1v4A49AAJpjuOmBaZ.k3C.AzREI-Uu2unKr.sjf6KYnJrBBbgQowKdFVe7QryJvY3Bujn1E-RE0FaNKReM.mqHLhVagHqdtSdshMsJTCa6gIoI.d7; SEARCH_RESULT_LIST_TYPE=%22single%22; FOLLOW_LIVE_POINT_INFO=%22MS4wLjABAAAAdCMshBZWi5t9dwh6jQnhKpcdPvCiaN3-hh_R-wvJpKc%2F1683043200000%2F0%2F0%2F1683036456622%22; douyin.com; strategyABtestKey=%221683601327.564%22; csrf_session_id=219e5b4fa422616fa7df0ea2ae60fc91; bd_ticket_guard_client_data=eyJiZC10aWNrZXQtZ3VhcmQtdmVyc2lvbiI6MiwiYmQtdGlja2V0LWd1YXJkLWl0ZXJhdGlvbi12ZXJzaW9uIjoxLCJiZC10aWNrZXQtZ3VhcmQtY2xpZW50LWNlcnQiOiItLS0tLUJFR0lOIENFUlRJRklDQVRFLS0tLS1cbk1JSUNFekNDQWJxZ0F3SUJBZ0lVU0VKOWlmb0xuY2p1NnBZa0YwdEJraWc4ekxJd0NnWUlLb1pJemowRUF3SXdcbk1URUxNQWtHQTFVRUJoTUNRMDR4SWpBZ0JnTlZCQU1NR1hScFkydGxkRjluZFdGeVpGOWpZVjlsWTJSellWOHlcbk5UWXdIaGNOTWpNd016RXhNRFF3TXpBeVdoY05Nek13TXpFeE1USXdNekF5V2pBbk1Rc3dDUVlEVlFRR0V3SkRcblRqRVlNQllHQTFVRUF3d1BZbVJmZEdsamEyVjBYMmQxWVhKa01Ga3dFd1lIS29aSXpqMENBUVlJS29aSXpqMERcbkFRY0RRZ0FFUExrL2M1UFJDSzdCaU9odVdsbnFGV2ZOU29pWEo2bXI1VnQ2M1NRWTBXL2ZoWmo2eE8zZDFIcExcbldkUkFKZjFnL2ZyV0pSUXlxVGpwOUdvZmkxSTk0Nk9CdVRDQnRqQU9CZ05WSFE4QkFmOEVCQU1DQmFBd01RWURcblZSMGxCQ293S0FZSUt3WUJCUVVIQXdFR0NDc0dBUVVGQndNQ0JnZ3JCZ0VGQlFjREF3WUlLd1lCQlFVSEF3UXdcbktRWURWUjBPQkNJRUlIKzdEb1JVdm1Wa3VUc1JQZUZsRS9zd1Jqd3dGL0lyQVZlL0tBRzRmamVkTUNzR0ExVWRcbkl3UWtNQ0tBSURLbForcU9aRWdTamN4T1RVQjdjeFNiUjIxVGVxVFJnTmQ1bEpkN0lrZURNQmtHQTFVZEVRUVNcbk1CQ0NEbmQzZHk1a2IzVjVhVzR1WTI5dE1Bb0dDQ3FHU000OUJBTUNBMGNBTUVRQ0lCMEpmUmpEYlppbTE3SWFcbml2QXYySnFuaFhJMmIvemlvNWhqaFdYRnQ3MVdBaUFsNmRaakpoa2Nsd0RodmxLY3pkWCtJTGpvT3puOThWWUVcbmVGR1JTdFR5QUE9PVxuLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLVxuIn0=; FOLLOW_NUMBER_YELLOW_POINT_INFO=%22MS4wLjABAAAAdCMshBZWi5t9dwh6jQnhKpcdPvCiaN3-hh_R-wvJpKc%2F1683648000000%2F0%2F1683601342417%2F0%22; download_guide=%223%2F20230509%22; VIDEO_FILTER_MEMO_SELECT=%7B%22expireTime%22%3A1684207515749%2C%22type%22%3A1%7D; home_can_add_dy_2_desktop=%221%22; passport_fe_beating_status=true; msToken=1CNo0yJRLKknVmCUL5zuqh7ACeDP8MihEnKdCRRUbwjMSua2OI4yeZ0D8ImJeh-e03R_S0OQKJ5fRjukzvWWPsirEh5sCWnoTD7COTzla0h6TrPURpoeZw==; msToken=mevoMEtfRjgzMmr_0iwMUBkFvWoYp_yZWmJ080IXyMQTtaLdYdALAfXPw-YaZB_UkUyGGiff8JJ8lXDA_UvlUuz8xkmhwQXsn7zCpIp3Cx_K1TSzfXKwRe6dv3rl42k=; tt_scid=4P1L-9CCK6IMalC0glfEv9jpBEA6hqMmHxl9Rd2wHGmenyOSgirY3kQiiUADYzVk545d");
        headerMap.add("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
        headerMap.add("user-agent","AppleWebKit/537.36 (KHTML, like Gecko)");
        headerMap.add("user-agent","Chrome/113.0.0.0 Safari/537.36");
        headerMap.add("User-Agent","Edg/113.0.1774.35");
        headerMap.add("Accept","*/*");
//        headerMap.add("Accept-Encoding","gzip");
//        headerMap.add("Accept-Encoding","deflate");
//        headerMap.add("Accept-Encoding","br");
//        headerMap.add("path","/aweme/v1/web/comment/list/?device_platform=webapp%26aid=6383%26channel=channel_pc_web%26aweme_id=7222430903706914060%26cursor=0%26count=20%26item_type=0%26insert_ids=%26rcFT=%26pc_client_type=1%26version_code=170400%26version_name=17.4.0%26cookie_enabled=true%26screen_width=1536%26screen_height=864%26browser_language=zh-CN%26browser_platform=Win32%26browser_name=Edge%26browser_version=113.0.1774.35%26browser_online=true%26engine_name=Blink%26engine_version=113.0.0.0%26os_name=Windows%26os_version=10%26cpu_core_num=8%26device_memory=8%26platform=PC%26downlink=1.45%26effective_type=3g%26round_trip_time=500%26webid=7209133947333920260%26msToken=1CNo0yJRLKknVmCUL5zuqh7ACeDP8MihEnKdCRRUbwjMSua2OI4yeZ0D8ImJeh-e03R_S0OQKJ5fRjukzvWWPsirEh5sCWnoTD7COTzla0h6TrPURpoeZw==%26X-Bogus=DFSzswVLnCsANep-tCxGKQppgiuY");
//        headerMap.add("Connection","keep-alive");
//        headerMap.add("pragma","no-cache");
//        headerMap.add("scheme","https");
//        headerMap.add("sec-ch-ua","Microsoft Edge\";v=\"113\", \"Chromium\";v=\"113\", \"Not-A.Brand\";v=\"24");
//        headerMap.add("sec-ch-ua-platform","Windows");
//        headerMap.add("sec-fetch-dest","empty");
//        headerMap.add("sec-fetch-mode","cors");
//        headerMap.add("sec-fetch-site","same-origin");
//        headerMap.add("referer","https://www.douyin.com/");
        httpHeaders.addAll(headerMap);
//        httpHeaders.setAccept(MediaType.ALL_VALUE);
//        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity getEntity=new HttpEntity(null,httpHeaders);
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, getEntity, Map.class);
        System.out.println(responseEntity);
        Map body = responseEntity.getBody();
        System.out.println(body);
    }

}
