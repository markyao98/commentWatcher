package com.markyao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.markyao.model.pojo.VideoInfo;

public interface VideoInfoMapper extends BaseMapper<VideoInfo> {

    int insertForId(VideoInfo videoInfo);

}
