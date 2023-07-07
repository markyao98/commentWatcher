package com.markyao.model.vo;

import com.markyao.model.pojo.CommentUser;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class CommentUserAndCdVo implements Serializable {

    private CommentUser user;

    private List<CommentDetailsVo> commentDetailList;

}
