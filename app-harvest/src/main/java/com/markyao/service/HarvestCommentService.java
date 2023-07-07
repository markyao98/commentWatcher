package com.markyao.service;

import com.markyao.model.pojo.Comment;
import com.markyao.model.pojo.CommentDetails;
import com.markyao.model.pojo.CommentUser;
import com.markyao.model.pojo.HarvestCommentUrl;
import org.apache.tomcat.util.json.ParseException;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

public interface HarvestCommentService {

    void startHarvestAllComments();

    void harvestComments(String url, HarvestCommentUrl c)throws IOException, ParseException;

    void harvestComments(LinkedHashMap<String, Object> stringObjectLinkedHashMap, HarvestCommentUrl c)throws IOException, ParseException;


    void harvestComments(LinkedHashMap<String, Object> stringObjectLinkedHashMap)throws IOException, ParseException;

    void saveCommentTransaction(CommentDetails commentDetails, CommentUser commentUser, Comment comment);

    void duplicateDealByCid(String cid);

    void deleteThreeTb(Long id, Long did, Long uid);
    void batchDeleteThreeTb(List<Long> id, List<Long> did, List<Long> uid);
}
