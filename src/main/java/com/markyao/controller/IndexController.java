package com.markyao.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.markyao.model.dto.RestData;
import com.markyao.model.pojo.VideoGroup;
import com.markyao.model.vo.VideoGroupVo;
import com.markyao.service.VideoGroupService;
import com.markyao.service.VideoInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class IndexController {
    @Autowired
    VideoInfoService videoInfoService;
    @Autowired
    VideoGroupService videoGroupService;
    /**
     * 返回分组的aid，同时要给与视频的标题信息
     * @return
     */
    @GetMapping("index")
    public RestData indexData(){
        return videoInfoService.getVideos();
    }
    /**
     * 返回分组的aid，同时要给与视频的标题信息
     * @return
     */
    @GetMapping("index/monitors")
    public RestData indexMonitorsData(){
        return videoInfoService.getVideos(1);
    }

    /**
     * 返回分组的aid，同时要给与视频的标题信息
     * @return
     */
    @GetMapping("index/groups")
    public RestData indexGroups(){
        List<VideoGroup> list = videoGroupService.list(new QueryWrapper<VideoGroup>().select("*"));
        List<VideoGroupVo>voList=new ArrayList<>(list.size());
        for (VideoGroup group : list) {
            VideoGroupVo vo=new VideoGroupVo();
            BeanUtils.copyProperties(group,vo);
            vo.setId(group.getId()+"");
            voList.add(vo);
        }
        return RestData.success(voList);
    }
}
