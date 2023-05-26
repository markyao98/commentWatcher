package com.markyao.service.harvest.impl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.ImmutableMap;
import com.markyao.mapper.HarvestCommentUrlMapper;
import com.markyao.mapper.VideoInfoMapper;
import com.markyao.model.pojo.HarvestCommentUrl;
import com.markyao.model.pojo.VideoInfo;
import com.markyao.service.harvest.RequestURLService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.devtools.Command;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v112.network.Network;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class RequestURLServiceImpl extends ServiceImpl<HarvestCommentUrlMapper, HarvestCommentUrl> implements RequestURLService {

    @Value("${douyin.video.searchLinkPrefix}")
    String searchLinkPrefix;

    @PostConstruct
    void init(){
        System.setProperty("webdriver.edge.driver", "E:\\develop\\webdriver\\msedgedriver.exe");
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
    /**
     * @Description 输入搜索类型,1为指定的url,2为搜索关键词
     *              获取评论的url，提供后续获取评论。并且获取视频的标题信息
     * @Author yaoruiwei
     * @Date  2023/5/14
     */
    @Override
    public void start0(String searchVal, int searchType){
        switch (searchType){
            case SEARCH_URL_TYPE:{
                log.info("指定URL爬取~");
                startHarvest(searchVal);
                break;
            }
            case SEARCH_KEY_TYPE:{
                log.info("关键词爬取~");
//                startHarvest(searchVal);
                break;
            }
        }
    }
    @Autowired
    ConcurrentHashMap<String,Object> stateMap;

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

    private void down(EdgeDriver driver){
        Actions actions=new Actions(driver);
        actions.sendKeys(Keys.DOWN).perform();
    }

    private void startHarvest(String startUrl) {
        EdgeDriver driver = new EdgeDriver();
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

            devTools.addListener(Network.requestWillBeSent(),event->{
                String url = event.getRequest().getUrl();
                if (url.startsWith("https://www.douyin.com/aweme/v1/web/comment/list")){
                    if (null!=videoInfo.getId()&&StringUtils.isEmpty(videoInfo.getAwemeId())){
                        updateVideoInfo(videoInfo, url);
                    }
                    saveUrlSingle(url);

                }
            });
            //1.打开评论列表
            sleep(1500);
            WebElement commentOpenBtn = driver.findElement(By.className("tzVl3l7w"));
//            commentOpenBtn = driver.findElement(By.cssSelector("#sliderVideo > div.JrMwkvQy.playerContainer.YFEqUSvt.dLCldFlr > div.zK9etl_2.slider-video > div > div.L1TH4HdO.d6KxRih3.positionBox > div > div:nth-child(3) > div > div.tzVl3l7w > div > svg"));
//            commentOpenBtn = driver.findElement(By.cssSelector("#sliderVideo > div.JrMwkvQy.playerContainer.YFEqUSvt.dLCldFlr > div.zK9etl_2.slider-video > div > div.L1TH4HdO.d6KxRih3.positionBox > div > div:nth-child(3) > div > div.tzVl3l7w > div > svg > g"));

            Optional.ofNullable(commentOpenBtn).ifPresent(c ->{
                log.info("打开评论列表,开始收集评论url~~");
//                actions.moveToElement(c).click();
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
            driver.quit();
        }
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

    private synchronized void saveUrlSingle(String url) {
        HarvestCommentUrl hcu=new HarvestCommentUrl();
        hcu.setUrl(url);
        hcu.setVideoId("");
        hcu.setAuthorId("");
        hcu.setId(0L);
        harvestCommentUrlMapper.insert(hcu);

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


    private static void keepReady(EdgeDriver driver) {
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
     * @Author yaoruiwei
     * @Date  2023/5/10
     */
    private static boolean isAll(EdgeDriver driver){
        //#merge-all-comment-container > div > div.sX7gMtFl.comment-mainContent.MR0IFMr1 > div.BbQpYS5o
        //#merge-all-comment-container > div > div.sX7gMtFl.comment-mainContent.MR0IFMr1 > div.BbQpYS5o
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
    private void preReady(EdgeDriver driver) {
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
