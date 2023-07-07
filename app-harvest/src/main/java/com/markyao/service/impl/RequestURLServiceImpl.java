package com.markyao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.ImmutableMap;
import com.markyao.async.ThreadService;
import com.markyao.mapper.HarvestCommentUrlMapper;
import com.markyao.mapper.VideoInfoMapper;
import com.markyao.model.pojo.HarvestCommentUrl;
import com.markyao.model.pojo.VideoInfo;

import com.markyao.service.HarvestCommentService;
import com.markyao.service.RequestURLService;
import com.markyao.service.core.HarvestCommentWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.json.ParseException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.Command;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v85.network.Network;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RequestURLServiceImpl extends ServiceImpl<HarvestCommentUrlMapper, HarvestCommentUrl> implements RequestURLService {

    @Value("${douyin.video.searchLinkPrefix}")
    String searchLinkPrefix;

    @PostConstruct
    void init(){
//        System.setProperty("webdriver.edge.driver", "E:\\develop\\webdriver\\msedgedriver.exe");
        System.setProperty("webdriver.edge.driver", "E:\\develop\\webdriver\\chromedriver.exe");
    }

    @Autowired
    HarvestCommentUrlMapper harvestCommentUrlMapper;
    @Autowired
    VideoInfoMapper videoInfoMapper;

    public final static String STOP_HARVEST_URL="STOP_HARVEST_URL";
    private final static int initCapacity=64;
    private final List<HarvestCommentUrl>harvestCommentUrls=new ArrayList<>(initCapacity);
    private final static int SEARCH_URL_TYPE=1;
    private final static int SEARCH_KEY_TYPE=2;
    private final static String DOUYIN_INDEX_PAGE="https://www.douyin.com/";
    private final static String TITLE_INFO="无标题数据";
    @Autowired
    ThreadService threadService;
    @Autowired
    HarvestCommentService harvestCommentService;

    @Autowired
    ConcurrentHashMap<String,Object> stateMap;



    /**
     * @Description 输入搜索类型,1为指定的url,2为搜索关键词
     *              获取评论的url，提供后续获取评论。并且获取视频的标题信息
     * @Author markyao
     * @Date  2023/5/14
     */
    @Override
    public void start0(String searchVal, int searchType,int isAllHarvest){
        switch (searchType){
            case SEARCH_URL_TYPE:{
                log.info("指定URL爬取~");
                threadService.startHarvest(this,searchVal);
                if (isAllHarvest==1){
                    new Thread(()->{
                        System.out.println("开始！！！===================");
                        while (true){
                            if (ALLInState==0){
                                log.info("暂无url数据~~");
                                sleep(1000);
                            }else if (ALLInState==1){
                                log.info("有url数据啦~~");
                                startHarvestComments();
                                sleep(1000);
                            }else if (ALLInState==2){
                                log.info("url数据处理完毕~~");
                                break;
                            }
                        }
                    }).start();
                }
                break;
            }
            case SEARCH_KEY_TYPE:{
                log.info("关键词爬取~");
                break;
            }
        }
    }




    /**
     * 全自动随机开始
     */
    @Override
    public void start() {
        start("",1);
    }

    @Override
    public void start(String searchVal, int searchType) {

    }



    @Override
    public void start0(){
        try {
            startHarvest(DOUYIN_INDEX_PAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, Network.GetResponseBodyResponse> responseBodyMap = new ConcurrentHashMap<>();
    private Map<String, HarvestCommentUrl> hcuMap = new ConcurrentHashMap<>();

//    private AtomicInteger ALLInState=new AtomicInteger(0);
    private int ALLInState=0;


    public void startHarvest(String startUrl) {
//        EdgeDriver driver = new EdgeDriver();
        ChromeDriver driver=new ChromeDriver();
        try {
            //todo 检查url
            driver.get(StringUtils.isEmpty(startUrl)?DOUYIN_INDEX_PAGE:startUrl);
            if (!StringUtils.isEmpty(startUrl)){
                log.info("!!!!!!!!!请抓紧时间验证!!!!!!!!!");
                log.info("!!!!!!!!!请抓紧时间验证!!!!!!!!!");
                log.info("!!!!!!!!!请抓紧时间验证!!!!!!!!!");
                log.info("!!!!!!!!!请抓紧时间验证!!!!!!!!!");
                log.info("!!!!!!!!!请抓紧时间验证!!!!!!!!!");
                log.info("!!!!!!!!!请抓紧时间验证!!!!!!!!!");
                log.info("!!!!!!!!!请抓紧时间验证!!!!!!!!!");

                sleep(15000);
            }
            sleep(500);
            //0.关闭验证窗口
            preReady(driver);

            DevTools devTools = driver.getDevTools();
            devTools.createSession();

            // 发送命令以确保 DevTools 准备好处理事件
            devTools.send(new Command<>("Network.enable", ImmutableMap.of()));

            //0.获取视频标题信息.
            VideoInfo videoInfo=new VideoInfo();
            List<WebElement> nu66P_ba_xhDopcQ__jn1tVXor = driver.findElements(By.xpath("/html/body/div[2]/div[1]/div[2]/div[4]/div[1]/div/div[2]/div/div[1]/div[1]/div/div[5]/div/div[1]/div[2]/div/div/span/span"));
            if (nu66P_ba_xhDopcQ__jn1tVXor.isEmpty()) {
                nu66P_ba_xhDopcQ__jn1tVXor=driver.findElements(By.cssSelector("#video-info-wrap > div.video-info-detail > div.title > div > div > span > span"));
                if (nu66P_ba_xhDopcQ__jn1tVXor.isEmpty()){
                    videoInfo.setTitleInfo(TITLE_INFO);
                    log.info("本视频获取不到视频标题信息");
                }else {
                    log.info("获取到视频标题信息");
                    WebElement e0 = nu66P_ba_xhDopcQ__jn1tVXor.get(0);
                    String titleInfos=getTitleInfos(e0);
                    videoInfo.setTitleInfo(titleInfos);
                }
            }else {
                log.info("获取到视频标题信息");
                WebElement e0 = nu66P_ba_xhDopcQ__jn1tVXor.get(0);
                String titleInfos=getTitleInfos(e0);
                videoInfo.setTitleInfo(titleInfos);
            }
            videoInfo.setWatchLink(searchLinkPrefix);
            videoInfoMapper.insertForId(videoInfo);

//            devTools.addListener(Network.requestWillBeSent(),event->{
//                String url = event.getRequest().getUrl();
//                if (url.startsWith("https://www.douyin.com/aweme/v1/web/comment/list")){
//                    if (null!=videoInfo.getId()&&StringUtils.isEmpty(videoInfo.getAwemeId())){
//                        updateVideoInfo(videoInfo, url);
//                    }
//                    //标志位 置为1
//                    ALLInState=1;
//                    HarvestCommentUrl hcu = saveUrlSingle(url);
//                    // 在请求发起时初始化响应对象，以便后续使用
//                    log.info("在请求发起时初始化响应对象，以便后续使用");
//                    Command<Network.GetResponseBodyResponse> responseBodyCmd = Network.getResponseBody(event.getRequestId());
//                    try {
//                        Network.GetResponseBodyResponse send = devTools.send(responseBodyCmd);
//                        responseBodyMap.put(event.getRequestId().toString(), send);
//                        hcuMap.put(event.getRequestId().toString(), hcu);
//                    } catch (Exception e) {
//                        log.info("未获取到数据!!!");
//                        e.printStackTrace();
//                    }
//                }
//            });


            devTools.addListener(Network.responseReceived(), event -> {
                String url = event.getResponse().getUrl();
                if (url.startsWith("https://www.douyin.com/aweme/v1/web/comment/list")) {
                    if (null!=videoInfo.getId()&&StringUtils.isEmpty(videoInfo.getAwemeId())){
                        updateVideoInfo(videoInfo, url);
                    }
                    //标志位 置为1
                    ALLInState=1;
                    HarvestCommentUrl hcu = saveUrlSingle(url);
                    // 在请求发起时初始化响应对象，以便后续使用
                    log.info("在请求发起时初始化响应对象，以便后续使用");
                    Command<Network.GetResponseBodyResponse> responseBodyCmd = Network.getResponseBody(event.getRequestId());
                    try {
                        Network.GetResponseBodyResponse send = devTools.send(responseBodyCmd);
                        responseBodyMap.put(event.getRequestId().toString(), send);
                        hcuMap.put(event.getRequestId().toString(), hcu);
                    } catch (Exception e) {
                        log.info("未获取到数据!!!");
                        e.printStackTrace();
                    }
                }
            });

            //1.打开评论列表
            sleep(1500);
            WebElement commentOpenBtn = driver.findElement(By.className("tzVl3l7w"));

            Optional.ofNullable(commentOpenBtn).ifPresent(c ->{
                log.info("打开评论列表,开始收集评论url~~");
                c.click();
                sleep(500);
                keepReady(driver);
                boolean isAll=false;

                Actions actions=new Actions(driver);
                WebDriverWait driverWait=new WebDriverWait(driver, Duration.ofSeconds(10));
                List<WebElement> plList = driverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("RHiEl2d8")));
                if (!plList.isEmpty()){
                    log.info("滑动鼠标到评论列表上面~");
                    actions.moveToElement(plList.get(0)).click().perform();
                    int noComments=0;
                    while (!stateMap.containsKey(STOP_HARVEST_URL)){
                        //向下滚动
                        actions.sendKeys(Keys.PAGE_DOWN).perform();
                        isAll=isAll(driver);
                        if (isAll){
                            noComments++;
                            sleep(10*1000);
                            if (noComments>3){
                                break;
                            }
                        }else {
                            noComments=0;
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    log.info("本视频评论url已经获取完毕啦~");
                }
                else {
                    log.error("未找到评论列表~");
                }
            });

            sleep(10000);
        } finally {
            ALLInState=2;
            driver.quit();
        }
    }

    public void startHarvestComments(){
        for (Map.Entry<String, Network.GetResponseBodyResponse> entry : responseBodyMap.entrySet()) {
            String requestId = entry.getKey();
            if (responseBodyMap.containsKey(requestId)){
                Network.GetResponseBodyResponse responseBody = responseBodyMap.get(requestId);
                HarvestCommentUrl hcu = hcuMap.get(requestId);
                if (responseBody != null) {
                    String body = responseBody.getBody();
                    try {
                        LinkedHashMap<String, Object> stringObjectLinkedHashMap =
                                HarvestCommentWorker.getStringObjectLinkedHashMap(body);
                        harvestCommentService.harvestComments(stringObjectLinkedHashMap,hcu);
                    } catch (IOException |ParseException e) {
                        e.printStackTrace();
                        log.error("处理url请求信息失败!!!");
                        return;
                    }
                }
                cleanBuf(requestId);
            }
        }
    }

    private void cleanBuf(String key) {
        responseBodyMap.remove(key);
        hcuMap.remove(key);
    }

    private synchronized void updateVideoInfo(VideoInfo videoInfo, String url) {
        if (StringUtils.isEmpty(videoInfo.getAwemeId()) && null!= videoInfo.getId()){
            StringBuilder ssb=new StringBuilder();
            for (int i = url.indexOf("&aweme_id=")+10; ; i++) {
                if (url.charAt(i)=='&'){
                    break;
                }
                ssb.append(url.charAt(i));
            }
            videoInfo.setAwemeId(ssb.toString());
            videoInfo.setWatchLink(videoInfo.getWatchLink()+ssb.toString());
            videoInfoMapper.updateById(videoInfo);
        }
    }


    /**
     * @Description 根据标签获取标题信息
     * @Author markyao
     * @Date  2023/5/15
     */
    private String getTitleInfos(WebElement e0) {

        List<WebElement> spans = e0.findElements(By.tagName("span"));
        StringBuilder sb=new StringBuilder();
        int i=1;
        for (WebElement span : spans) {
            WebElement valueEle=null;
            try {
                valueEle = span.findElement(By.tagName("span")).findElement(By.tagName("span"));
            } catch (Exception e) {}
            try {
                if (null==valueEle) {
                    valueEle = span.findElement(By.tagName("a")).findElement(By.tagName("span"));
                }
            } catch (Exception e) {}
            i++;
            if (null!=valueEle){
                sb.append(valueEle.getText()+" ");
            }
        }
        return sb.toString();
    }

    private synchronized HarvestCommentUrl saveUrlSingle(String url) {
        HarvestCommentUrl hcu=new HarvestCommentUrl();
        hcu.setUrl(url);
        hcu.setVideoId("");
        hcu.setAuthorId("");
        hcu.setId(0L);
        hcu.setCreateTime(new Date());
        hcu.setUpdateTime(new Date());
        harvestCommentUrlMapper.insertForId(hcu);
        return hcu;

    }
    private void sleep(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void saveUrl(String url) {
        if (harvestCommentUrls.size()>=initCapacity){
            harvestCommentUrlMapper.insertBatch(harvestCommentUrls);
            harvestCommentUrls.clear();
        }
        HarvestCommentUrl hcu=new HarvestCommentUrl();
        hcu.setUrl(url);
        hcu.setVideoId("");
        hcu.setAuthorId("");
        hcu.setId(0L);
        harvestCommentUrls.add(hcu);
    }


    private static void keepReady(WebDriver driver) {
        List<WebElement> elements = driver.findElements(By.className("related-video-card-login-guide__footer-close"));
        if (!elements.isEmpty()){
            WebElement keepLook = elements.get(0);
            Optional.ofNullable(keepLook).ifPresent(k->{
                log.info("继续看评论~ 不登录");
                try {
                    keepLook.click();
                } catch (Exception e) {
                    log.info("未弹出阻挡页面，无需点击");
                }
            });
        }
  }

    /**
     * @Description 判断鼠标滚轮是否滑动到底部
     * @Author markyao
     * @Date  2023/5/10
     */
    private static boolean isAll(WebDriver driver){
        WebElement webElement=null;
        try {
            webElement = driver.findElements(By.cssSelector("#merge-all-comment-container > div > div.sX7gMtFl.comment-mainContent.MR0IFMr1 > div.BbQpYS5o")).get(0);
        } catch (Exception e) {
            return false;
        }
        if (webElement!=null){
            return true;
        }
        return false;
    }
    private void preReady(WebDriver driver) {
        //验证登录窗口
        List<WebElement> elements = driver.findElements(By.className("dy-account-close"));
        WebElement divClose=null;
        if (!elements.isEmpty()){
            divClose = elements.get(0);
            Optional.ofNullable(divClose).ifPresent(d->{
                log.info("关闭验证窗口~");
                d.click();
            });
        }


        //提示滚轮信息-- class za0nfexc
        List<WebElement> scs = driver.findElements(By.className("za0nfexc"));
        if (!scs.isEmpty()){
            WebElement scrollDiv = scs.get(0);
            WebElement finalDivClose = divClose;
            Optional.ofNullable(scrollDiv).ifPresent(s->{
                if (null != finalDivClose){
                    log.info("等待验证窗口关闭~");
                    sleep(500);
                }
                log.info("下上滚动~");
                Actions actions=new Actions(driver);
                actions.sendKeys(Keys.ARROW_DOWN).perform();
                sleep(500);
                actions.sendKeys(Keys.chord(Keys.CONTROL, Keys.ADD)).perform();
                sleep(500);
                actions.sendKeys(Keys.chord(Keys.CONTROL, Keys.ARROW_UP)).perform();
                sleep(500);
                actions.sendKeys(Keys.ARROW_UP).perform();
                sleep(500);


                List<WebElement> zhibos = driver.findElements(By.className("FqE3q4s5"));
                if (!zhibos.isEmpty()){
                    log.info("遇到直播了，继续向下滚动~");
                    actions.sendKeys(Keys.ARROW_DOWN).perform();
                    actions.sendKeys(Keys.chord(Keys.CONTROL, Keys.ADD)).perform();
                }

            });
        }

    }
}
