package com.markyao.model.vo;

import com.markyao.model.pojo.CommentDetails;
import com.markyao.model.pojo.CommentUser;
import lombok.Data;

@Data
public class CommentVo {

    private Long id;
    private CommentUser commentUser;
    private CommentDetailsVo commentDetails;
    private String userCardLink;
}
