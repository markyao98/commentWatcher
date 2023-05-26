package com.markyao.model.vo;

import com.markyao.model.pojo.CommentDetails;
import com.markyao.model.pojo.CommentUser;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class CommentUserVo {

    private CommentUser user;

    private List<CommentDetailsVo> commentDetailList;


}
