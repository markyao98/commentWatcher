package com.markyao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.markyao.model.dto.RestData;
import com.markyao.model.pojo.VideoInfo;

public interface VideoInfoService extends IService<VideoInfo> {
    RestData getVideos();
    RestData getVideos(int type);


    VideoInfo getVideo(String aid,String title);
}
