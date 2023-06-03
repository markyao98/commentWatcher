package com.markyao.controller;

import com.markyao.model.dto.RestData;
import com.markyao.model.pojo.VideoGroup;
import com.markyao.model.pojo.VideoInfo;
import com.markyao.model.vo.VideoInfoVo;
import com.markyao.service.VideoGroupService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
public class VideoGroupController {
    @Autowired
    VideoGroupService videoGroupService;

    @GetMapping("group/byG")
    public RestData videosByGNotIN(){
        List<VideoInfo>videos=videoGroupService.getVideosNotIn();
        List<VideoInfoVo>videoInfoVos=new ArrayList<>(videos.size());
        for (VideoInfo video : videos) {
            VideoInfoVo vo=new VideoInfoVo();
            BeanUtils.copyProperties(video,vo);
            vo.setId(video.getId()+"");
            videoInfoVos.add(vo);
        }
        return RestData.success(videoInfoVos);
    }
    @GetMapping("group/byG/{id}")
    public RestData videosByG(@PathVariable("id")String id){
        List<VideoInfo>videos=videoGroupService.getVideosByGid(id);
        return RestData.success(videos);
    }
    @GetMapping("groupinfo/{id}")
    public RestData groupinfo(@PathVariable("id")String id){
        VideoGroup videoGroup = videoGroupService.getById(id);
        return RestData.success(videoGroup);
    }
    //addgroup
    @PostMapping("/addgroup")
    public RestData handleFormSubmit(@RequestParam("desc") String desc,
                                     @RequestParam("title") String title,
                                     @RequestParam("img") MultipartFile img) {
        // 在这里处理表单数据
        // 根据需要进行相应的操作
        videoGroupService.saveGroup(desc,title,img);
        return RestData.success(null);
    }

    @GetMapping("/group/addVideo")
    public RestData groupaddVideo(@RequestParam("vids") String[] vids,@RequestParam("gid")String gid){
        videoGroupService.addVideosForGroup(vids,gid);
        return RestData.success(null);
    }
}
