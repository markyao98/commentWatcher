package com.markyao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.markyao.mapper.VideoInfoMapper;
import com.markyao.model.dto.RestData;
import com.markyao.model.pojo.VideoInfo;
import com.markyao.model.vo.VideoInfoVo;
import com.markyao.service.VideoInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VideoInfoServiceImpl extends ServiceImpl<VideoInfoMapper, VideoInfo> implements VideoInfoService {


    @Override
    public RestData getVideos() {
        List<VideoInfo> videoInfos = this.baseMapper.selectList(new QueryWrapper<VideoInfo>());
        List<VideoInfoVo>videoInfoVos=new ArrayList<>(videoInfos.size());
        for (VideoInfo videoInfo : videoInfos) {
            VideoInfoVo vo=new VideoInfoVo();
            BeanUtils.copyProperties(videoInfo,vo);
            videoInfoVos.add(vo);
        }
        return RestData.success(videoInfoVos);
    }

    @Override
    public RestData getVideos(int type) {
        if (type==1){
            //获取可监控视频的aids
            List<VideoInfo> videoInfos = this.baseMapper.selectList(new QueryWrapper<VideoInfo>().eq("can_monitor",true));
            List<VideoInfoVo>videoInfoVos=new ArrayList<>(videoInfos.size());
            for (VideoInfo videoInfo : videoInfos) {
                VideoInfoVo vo=new VideoInfoVo();
                BeanUtils.copyProperties(videoInfo,vo);
                videoInfoVos.add(vo);
            }
            return RestData.success(videoInfoVos);
        }
        return getVideos();
    }
    @Value("${douyin.video.searchLinkPrefix}")
    String searchLinkPrefix;

    @Override
    public VideoInfo getVideo(String aid,String title) {
        VideoInfo videoInfo=new VideoInfo();
        videoInfo.setAwemeId(aid).setTitleInfo(title).setWatchLink(searchLinkPrefix+aid);
        return videoInfo;
    }
}
