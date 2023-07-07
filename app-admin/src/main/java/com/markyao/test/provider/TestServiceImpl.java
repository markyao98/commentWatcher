package com.markyao.test.provider;

import com.markyao.model.vo.CommentDetailsVo;
import publicInterface.test.TestService;

import java.util.ArrayList;
import java.util.List;

public class TestServiceImpl implements TestService {
    @Override
    public CommentDetailsVo getCommentDetailsVo() {
        CommentDetailsVo commentDetailsVo = new CommentDetailsVo();
        commentDetailsVo.setText("测试text!!!").setVideoTitle("测试title!!");
        return commentDetailsVo;
    }

    @Override
    public List<CommentDetailsVo> getCommentDetailsVoList() {
        CommentDetailsVo commentDetailsVo = new CommentDetailsVo();
        commentDetailsVo.setText("测试text!!!").setVideoTitle("测试title!!");
        CommentDetailsVo commentDetailsVo2 = new CommentDetailsVo();
        commentDetailsVo.setText("测试text22!!!").setVideoTitle("测试title22!!");
        CommentDetailsVo commentDetailsVo3 = new CommentDetailsVo();
        commentDetailsVo.setText("测试text33!!!").setVideoTitle("测试title33!!");
        List<CommentDetailsVo>list=new ArrayList<>();
        list.add(commentDetailsVo);
        list.add(commentDetailsVo2);
        list.add(commentDetailsVo3);
        return list;
    }
}
