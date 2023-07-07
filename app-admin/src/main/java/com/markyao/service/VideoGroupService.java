package com.markyao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.markyao.model.pojo.VideoGroup;
import com.markyao.model.pojo.VideoInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoGroupService extends IService<VideoGroup> {
    List<VideoInfo> getVideosByGid(String id);

    List<VideoInfo> getVideosNotIn();

    void saveGroup(String desc, String title, MultipartFile img);

    void addVideosForGroup(String[] vids,String gid);

    VideoGroup getGroupByVid(Long vid);
}
