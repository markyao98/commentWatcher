package com.markyao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.markyao.model.pojo.HarvestCommentUrl;

import java.util.List;


public interface HarvestCommentUrlMapper extends BaseMapper<HarvestCommentUrl> {

    void insertBatch(List<HarvestCommentUrl>list);

    void insertForId(HarvestCommentUrl hcu);
}
