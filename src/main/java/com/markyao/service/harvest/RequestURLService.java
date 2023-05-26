package com.markyao.service.harvest;

import com.baomidou.mybatisplus.extension.service.IService;
import com.markyao.model.pojo.HarvestCommentUrl;

public interface RequestURLService extends IService<HarvestCommentUrl> {


    void start0();

    void start0(String searchVal, int searchType);

    void start();

    void start(String searchVal, int searchType);


}
