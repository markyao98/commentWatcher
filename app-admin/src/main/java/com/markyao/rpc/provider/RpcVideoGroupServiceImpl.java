package com.markyao.rpc.provider;

import MicroRpc.framework.commons.ServiceProvider;
import com.markyao.model.dto.RestData;
import com.markyao.model.pojo.VideoGroup;
import com.markyao.model.pojo.VideoInfo;
import com.markyao.model.vo.VideoInfoVo;
import com.markyao.service.VideoGroupService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import publicInterface.RpcVideoGroupService;

import java.util.ArrayList;
import java.util.List;
@Component
@ServiceProvider
public class RpcVideoGroupServiceImpl implements RpcVideoGroupService {
    @Autowired
    VideoGroupService videoGroupService;
    @Override
    public RestData videosByGNotIN() {
        List<VideoInfo> videos=videoGroupService.getVideosNotIn();
        List<VideoInfoVo>videoInfoVos=new ArrayList<>(videos.size());
        for (VideoInfo video : videos) {
            VideoInfoVo vo=new VideoInfoVo();
            BeanUtils.copyProperties(video,vo);
            vo.setId(video.getId()+"");
            videoInfoVos.add(vo);
        }
        return RestData.success(videoInfoVos);
    }

    @Override
    public RestData videosByG(String id) {
        List<VideoInfo>videos=videoGroupService.getVideosByGid(id);
        return RestData.success(videos);
    }

    @Override
    public RestData groupinfo(String id) {
        VideoGroup videoGroup = videoGroupService.getById(id);
        return RestData.success(videoGroup);
    }

    @Override
    public RestData handleFormSubmit(String desc, String title, MultipartFile img) {
        // 在这里处理表单数据
        // 根据需要进行相应的操作
        videoGroupService.saveGroup(desc,title,img);
        return RestData.success(null);
    }

    @Override
    public RestData groupaddVideo(String[] vids, String gid) {
        videoGroupService.addVideosForGroup(vids,gid);
        return RestData.success(null);
    }
}
