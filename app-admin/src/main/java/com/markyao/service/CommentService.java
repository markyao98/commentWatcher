package com.markyao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.markyao.model.dto.RestData;
import com.markyao.model.pojo.Comment;

public interface CommentService extends IService<Comment> {



    RestData pages(int current, int pageSize);
    RestData pages(String awemeId,int current, int pageSize,int searchType,Object searchMsg,String sortType) ;

//    void startHarvestAllComments();
//
//    void harvestComments(String url, HarvestCommentUrl c)throws IOException, ParseException;
//
//    void harvestComments(LinkedHashMap<String, Object> stringObjectLinkedHashMap,HarvestCommentUrl c)throws IOException, ParseException;
//
//
//    void harvestComments(LinkedHashMap<String, Object> stringObjectLinkedHashMap)throws IOException, ParseException;
//
//    void saveCommentTransaction(CommentDetails commentDetails, CommentUser commentUser, Comment comment);
//
//    void duplicateDealByCid(String cid);
//
//    void deleteThreeTb(Long id, Long did, Long uid);
//    void batchDeleteThreeTb(List<Long> id, List<Long> did, List<Long> uid);
}
