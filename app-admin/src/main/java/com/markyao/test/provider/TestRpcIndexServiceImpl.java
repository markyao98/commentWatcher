package com.markyao.test.provider;

import MicroRpc.framework.commons.ServiceProvider;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.markyao.model.dto.RestData;
import com.markyao.model.pojo.VideoGroup;
import com.markyao.model.vo.VideoGroupVo;
import com.markyao.service.VideoGroupService;
import com.markyao.service.VideoInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import publicInterface.test.TestRpcIndexService;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试我的rpc
 */
@Component
@ServiceProvider
public class TestRpcIndexServiceImpl implements TestRpcIndexService {
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
