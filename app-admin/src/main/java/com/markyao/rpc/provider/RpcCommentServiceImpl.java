package com.markyao.rpc.provider;

import MicroRpc.framework.commons.ServiceProvider;
import com.markyao.es.CommentEsService;
import com.markyao.model.dto.RestData;
import com.markyao.service.CommentService;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import publicInterface.RpcCommentService;

import java.lang.reflect.Method;

@Component
@ServiceProvider
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
        return commentService.pages(awemeId,currentPage, pageSize,searchType,searchMsg,sortType);
    }

    @Override
    public RestData pages(String awemeId, Integer currentPage, Integer pageSize, Integer searchType, String searchMsg, String sortType, Integer sortRet) {
        return commentEsService.pages(awemeId,currentPage, pageSize,searchType,searchMsg,sortType,sortRet);

    }

    @Override
    public RestData pages(String[] awemeIds, Integer currentPage, Integer pageSize, Integer searchType, String searchMsg, String sortType, Integer sortRet) {
        return commentEsService.pages(awemeIds,currentPage, pageSize,searchType,searchMsg,sortType,sortRet);
    }

    public static void main(String[] args) throws NoSuchMethodException {
        Method method = RpcCommentServiceImpl.class.
                getMethod("pages", new Class[]{String.class, Integer.class, Integer.class,
                        Integer.class, String.class, String.class, int.class});
        System.out.println(method);
    }

//    @Override
//    public RestData listPage(Integer currentPage, Integer pageSize) {
//        return commentService.pages(currentPage, pageSize);
//
//    }
}
