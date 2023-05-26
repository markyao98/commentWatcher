package com.markyao.controller;

import com.markyao.model.dto.RestData;
import com.markyao.service.VideoInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {
    @Autowired
    VideoInfoService videoInfoService;

    /**
     * 返回分组的aid，同时要给与视频的标题信息
     * @return
     */
    @GetMapping("index")
    public RestData indexData(){
        return videoInfoService.getVideos();
    }
}
