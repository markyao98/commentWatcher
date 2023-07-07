package com.markyao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.markyao.model.pojo.HarvestCommentUrl;

public interface RequestURLService extends IService<HarvestCommentUrl> {


    void start0();

    void start0(String searchVal, int searchType,int isAllHarvest);

    void start();

    void start(String searchVal, int searchType);


}
