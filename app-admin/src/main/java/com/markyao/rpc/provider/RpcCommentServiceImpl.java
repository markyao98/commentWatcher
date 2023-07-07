package com.markyao.rpc.provider;

import com.markyao.es.CommentEsService;
import com.markyao.model.dto.RestData;
import com.markyao.service.CommentService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import publicInterface.RpcCommentService;

@DubboService
public class RpcCommentServiceImpl implements RpcCommentService {
    @Autowired
    CommentService commentService;
    @Autowired
    CommentEsService commentEsService;
    @Override
    public RestData listPage(Integer currentPage, Integer pageSize) {
        return commentService.pages(currentPage, pageSize);
    }

    @Override
    public RestData pages(String awemeId, Integer currentPage, Integer pageSize, Integer searchType, String searchMsg, String sortType) {
//        return commentService.pages(awemeId,currentPage, pageSize,searchType,searchMsg,sortType);
        return commentService.pages(awemeId,currentPage, pageSize,searchType,searchMsg,sortType);
    }

    @Override
    public RestData pages(String awemeId, Integer currentPage, Integer pageSize, Integer searchType, String searchMsg, String sortType, int sortRet) {
        return commentEsService.pages(awemeId,currentPage, pageSize,searchType,searchMsg,sortType,sortRet);

    }


//    @Override
//    public RestData listPage(Integer currentPage, Integer pageSize) {
//        return commentService.pages(currentPage, pageSize);
//
//    }
}
