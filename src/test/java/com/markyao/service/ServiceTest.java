package com.markyao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.markyao.httpHarvest.HttpService;
import com.markyao.httpHarvest.HttpService3;
import com.markyao.mapper.CommentMapper;
import com.markyao.mapper.HarvestCommentUrlMapper;
import com.markyao.model.pojo.Comment;
import com.markyao.model.pojo.HarvestCommentUrl;
import com.markyao.service.harvest.impl.CommentServiceImpl;
import com.markyao.service.harvest.impl.RequestURLServiceImpl;
import org.apache.tomcat.util.json.ParseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ServiceTest {
    @Autowired
    HttpService httpService;

    @Test
    void t1(){
        httpService.test1();

    }

    @Autowired
    HttpService3 httpService3;
    @Test
    void t3(){
        try {
            httpService3.test();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Autowired
    HarvestCommentUrlMapper harvestCommentUrlMapper;

    @Test
    void t4(){
        HarvestCommentUrl hcu=new HarvestCommentUrl();
        hcu.setId(0l);
        hcu.setAuthorId("a");
        hcu.setVideoId("b");
        hcu.setUrl("c");
 HarvestCommentUrl hcu1=new HarvestCommentUrl();
        hcu.setId(0l);
        hcu.setAuthorId("aa");
        hcu.setVideoId("ba");
        hcu.setUrl("aa");
 HarvestCommentUrl hcu2=new HarvestCommentUrl();
        hcu.setId(0l);
        hcu.setAuthorId("ab");
        hcu.setVideoId("bb");
        hcu.setUrl("cb");
 HarvestCommentUrl hcu3=new HarvestCommentUrl();
        hcu.setId(0l);
        hcu.setAuthorId("ac");
        hcu.setVideoId("bc");
        hcu.setUrl("cc");

        List<HarvestCommentUrl>urls=new ArrayList<>();
        urls.add(hcu);
        urls.add(hcu1);
        urls.add(hcu2);
        urls.add(hcu3);
        harvestCommentUrlMapper.insertBatch(urls);
    }


    @Autowired
    RequestURLServiceImpl saveRequestURL;

    @Test
    void t5(){
        saveRequestURL.start0();
    }

    @Autowired
    CommentServiceImpl commentService;

    /**
     * 收集指定url的评论
     */
    @Test
    void t6(){
//        String url="https://www.douyin.com/aweme/v1/web/comment/list/?device_platform=webapp&aid=6383&channel=channel_pc_web&aweme_id=7231787156044352825&cursor=2360&count=20&item_type=0&insert_ids=&rcFT=&pc_client_type=1&version_code=170400&version_name=17.4.0&cookie_enabled=true&screen_width=1536&screen_height=864&browser_language=zh-CN&browser_platform=Win32&browser_name=Edge&browser_version=113.0.1774.35&browser_online=true&engine_name=Blink&engine_version=113.0.0.0&os_name=Windows&os_version=10&cpu_core_num=8&device_memory=8&platform=PC&downlink=1.15&effective_type=3g&round_trip_time=550&webid=7232992039301514792&msToken=v9tjNxCpJhUwiBwl9zixF_DJZLSVMy8pzsNQ8ujLNVRQmi8zhKNgrNJW1ucYbDeeWP2qq8HIQFl2TzLcswqMl5N2GcDuY02driYN5gzy1QarFnORRpthiYLgQ5bzBIs=&X-Bogus=DFSzswRLJwtANxvhttfZSl9WX7jC";
        String url="https://www.douyin.com/aweme/v1/web/comment/list/?device_platform=webapp&aid=6383&channel=channel_pc_web&aweme_id=7233470811242319108&cursor=0&count=20&item_type=0&insert_ids=&rcFT=&pc_client_type=1&version_code=170400&version_name=17.4.0&cookie_enabled=true&screen_width=1536&screen_height=864&browser_language=zh-CN&browser_platform=Win32&browser_name=Edge&browser_version=113.0.1774.42&browser_online=true&engine_name=Blink&engine_version=113.0.0.0&os_name=Windows&os_version=10&cpu_core_num=8&device_memory=8&platform=PC&downlink=10&effective_type=4g&round_trip_time=150&webid=7233783947908859403&msToken=gGkV_K79_CgoknlGzFJQ-J3v5MGdaOOnsDAj6auNxOhGdBaC-v59hPG_neEPQcG3cxt0yb8dDS0QXs1C6TzZ2pLlat5xTNPvFMAVT5GEN5vnSdHGA6YN2UBF-eh1n2A=&X-Bogus=DFSzswRLuNbANjqvttU6sF9WX7jv";
//        try {
//            commentService.harvestComments(url,null);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
    }

    @Test
    void t7(){
        saveRequestURL.start0();
    }

    /**
     * 收集url
     */
    @Test
    void t8(){
        //7195333866054257952
//        String url="https://www.douyin.com/discover?modal_id=7231850083292024099";
//        String url="https://www.douyin.com/discover?modal_id=7228628807190646071";
        String url="https://www.douyin.com/discover?modal_id=7233470811242319108";
//7219559025464184125
        saveRequestURL.start0(url,1);
    }

    /**
     * 处理所有收集好的url，收集评论
     */
    @Test
    void t9(){
        commentService.startHarvestAllComments();
    }

    @Autowired
    CommentMapper commentMapper;
    @Test
    void t10(){
        Page<Comment>p=new Page<>(1,3);
        commentMapper.selectPage(p, null);
        System.out.println(p.getTotal());
        System.out.println(p.getRecords());
    }
}
