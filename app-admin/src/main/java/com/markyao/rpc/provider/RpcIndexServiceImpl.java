package com.markyao.rpc.provider;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.markyao.model.dto.RestData;
import com.markyao.model.pojo.VideoGroup;
import com.markyao.model.vo.VideoGroupVo;
import com.markyao.service.VideoGroupService;
import com.markyao.service.VideoInfoService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import publicInterface.RpcIndexService;

import java.util.ArrayList;
import java.util.List;

/**
 * 提供远程调用实现类————视频信息相关
 */
@DubboService
public class RpcIndexServiceImpl implements RpcIndexService {
    @Autowired
    VideoInfoService videoInfoService;
    @Autowired
    VideoGroupService videoGroupService;
    @Override
    public RestData getVideos() {
        return videoInfoService.getVideos();
    }

    @Override
    public RestData indexMonitorsData() {
        return videoInfoService.getVideos(1);
    }

    @Override
    public RestData indexGroups() {
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
