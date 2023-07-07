package com.markyao.controller;

import com.markyao.model.dto.RestData;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import publicInterface.RpcCommentDetailService;
import publicInterface.RpcCommentService;

import java.text.ParseException;

@RestController
@RequestMapping("comments")
public class CommentController {

    @DubboReference
    RpcCommentService rpcCommentService;
    @DubboReference(timeout = 60*1000)
    RpcCommentDetailService rpcCommentDetailService;

    @GetMapping("list")
    public RestData listPage(@RequestParam(value = "page",defaultValue = "1") Integer currentPage,
                             @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        return rpcCommentService.listPage(currentPage,pageSize);

    }

    @GetMapping("list/{awemeId}")
    public RestData listPage(@RequestParam(value = "page",defaultValue = "1") Integer currentPage,
                             @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                             @PathVariable("awemeId")String awemeId,
                             @RequestParam(value = "searchType",defaultValue = "0")Integer searchType,
                             @RequestParam(value = "searchMsg")String searchMsg,
                             @RequestParam(value = "sortType",defaultValue = "0")String sortType,
                             @RequestParam(value = "sortRet",defaultValue = "1",required = false)String sortRet
                             ) throws ParseException {
        if (sortRet.equals("undefined") || StringUtils.isEmpty(sortRet)){
            sortRet="1";
        }
        return rpcCommentService.pages(awemeId,currentPage, pageSize,searchType,searchMsg,sortType,Integer.valueOf(sortRet));
    }
    /**
     * 更新该视频下的点赞数
//     */
    @GetMapping("updateStatus/{awemeId}")
    public RestData updateStatus(@PathVariable("awemeId")String awemeId){
        return rpcCommentDetailService.updateStatus(awemeId);
    }
}