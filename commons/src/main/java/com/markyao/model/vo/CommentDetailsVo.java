package com.markyao.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class CommentDetailsVo implements Serializable {
    private String id;
    private String awemeId;
    private String cid;
    private String ipLabel;
    private String createTime;
    private Integer diggCount;
    private Integer replyCommentTotal;
    private String text;
    private Boolean isAuthorDigged;//作者赞过
    private String videoTitle;
    private Integer cur;
    private Integer count;
}
