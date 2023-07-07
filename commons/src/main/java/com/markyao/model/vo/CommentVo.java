package com.markyao.model.vo;

import com.markyao.model.pojo.CommentUser;
import lombok.Data;

import java.io.Serializable;

@Data
public class CommentVo implements Serializable {

    private Long id;
    private CommentUser commentUser;
    private CommentUserVo commentUserVo;
    private CommentDetailsVo commentDetails;
    private String userCardLink;
    private Boolean isMonitored; //是否被监控过

}
