package com.markyao.asnc;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.markyao.mapper.VideoInfoMapper;
import com.markyao.model.pojo.VideoInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ThreadService {

    @Async("taskExecutor")
    public void updateVideoCommentsTotals(long total,String awemeId, VideoInfoMapper videoInfoMapper){
        VideoInfo videoInfo = videoInfoMapper.selectOne(new QueryWrapper<VideoInfo>().eq("aweme_id", awemeId));
        if (videoInfo.getTotals()==null || videoInfo.getTotals()<total){
            videoInfo.setTotals(Math.toIntExact(total));
            log.info("更新视频评论总数~");
            videoInfoMapper.updateById(videoInfo);
        }
    }
}
