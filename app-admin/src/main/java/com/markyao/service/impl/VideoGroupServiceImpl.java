package com.markyao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.markyao.mapper.VideoGroupMapper;
import com.markyao.model.pojo.VideoGroup;
import com.markyao.model.pojo.VideoInfo;
import com.markyao.service.VideoGroupService;
import com.markyao.service.VideoInfoService;
import com.markyao.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class VideoGroupServiceImpl extends ServiceImpl<VideoGroupMapper, VideoGroup> implements VideoGroupService {
//    private final static String dictoryPath=VideoInfoServiceImpl.class.getClassLoader().getResource("static").getPath()+"/"+"groupsImg/";
    private final static String dictoryPath="";

    @Override
    public List<VideoInfo> getVideosByGid(String id) {
        List<VideoInfo>infos=this.baseMapper.getVideosByGid(id);
        return infos;
    }

    @Override
    public List<VideoInfo> getVideosNotIn() {
        return this.baseMapper.getVideosByNotIn();
    }

    @Override
    public void saveGroup(String desc, String title, MultipartFile img) {
        VideoGroup group=new VideoGroup();
        group.setTitle(title).setDescMsg(desc);
        String fileName = img.getOriginalFilename();
        try {
            String src = FileUtils.writeToDest(dictoryPath, fileName, img.getInputStream());
            group.setImg(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.save(group);
    }
    @Autowired
    VideoInfoService videoInfoService;
    @Override
    public void addVideosForGroup(String[] vids,String gid) {
        this.getBaseMapper().addVideosForGroup(vids,gid);
    }

    @Override
    public VideoGroup getGroupByVid(Long vid) {
        VideoGroup videoGroup=this.baseMapper.selectByVid(vid);
        return videoGroup;
    }
}
