package com.markyao.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.markyao.mapper.CommentDetailsMapper;
import com.markyao.mapper.CommentMapper;
import com.markyao.mapper.CommentUserMapper;
import com.markyao.model.pojo.Comment;
import com.markyao.model.pojo.CommentDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 去重工具
 */
@Service
public class DuplicateService {
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    CommentDetailsMapper commentDetailsMapper;
    @Autowired
    CommentUserMapper commentUserMapper;


    @Transactional(rollbackFor = Exception.class)
    public void deleteAllByCids(String cids[]){
        List<CommentDetails> commentDetails = commentDetailsMapper.selectList(new QueryWrapper<CommentDetails>().in("cid", cids).select("id"));
        //获取cdids
        List<Long> cdids =commentDetails.stream().map(c->c.getId()).collect(Collectors.toList());

        //通过cdids查询评论表,获取用户
        List<Comment> commentList = commentMapper.selectList(new QueryWrapper<Comment>().in("did", cdids).select("id", "uid"));

        //获取评论主键,获取用户主键
        List<Long>commentIds=commentList.stream().map(c->c.getId()).collect(Collectors.toList());
        List<Long>userIds=commentList.stream().map(c->c.getUid()).collect(Collectors.toList());

        //先删除用户主键
        commentUserMapper.deleteBatchIds(userIds);
        //删除comment
        commentMapper.deleteBatchIds(commentIds);
        //删除详情
        commentDetailsMapper.deleteBatchIds(cdids);
    }
}
