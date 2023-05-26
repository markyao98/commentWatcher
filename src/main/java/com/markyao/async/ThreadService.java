package com.markyao.async;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.markyao.mapper.HarvestCommentUrlMapper;
import com.markyao.model.pojo.HarvestCommentUrl;
import com.markyao.service.harvest.CommentService;
import com.markyao.service.harvest.RequestURLService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ThreadService {
    @Autowired
    HarvestCommentUrlMapper harvestCommentUrlMapper;

    @Async("taskExecutor")
    public void updateUrlStatus(HarvestCommentUrlMapper harvestCommentUrlMapper, HarvestCommentUrl harvestCommentUrl) {
        harvestCommentUrl.setIsDeal(true);
        harvestCommentUrlMapper.update(harvestCommentUrl,new UpdateWrapper<HarvestCommentUrl>().eq("id",harvestCommentUrl.getId()));
        log.info("更新url收集状态..");
    }


    @Async("taskExecutor")
    public void startHarvestUrl(String url, RequestURLService requestURLService) {
        log.info("异步收集~");
        requestURLService.start0(url,1);
    }

    @Async("taskExecutor")
    public void startHarvestAllComments(CommentService commentService) {
        log.info("异步采集评论数据~~");
        commentService.startHarvestAllComments();
    }

    @Async("taskExecutor")
    public void deleteOldUrls() {
        harvestCommentUrlMapper.delete(new QueryWrapper<HarvestCommentUrl>().eq("is_deal",1));
        log.info("异步删除旧数据~~");
    }
}