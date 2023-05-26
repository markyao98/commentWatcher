package com.markyao.model.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@TableName("harvest_comment_details")
@Accessors(chain = true)
public class CommentDetails {
    private Long id;
    private String awemeId;
    private String cid;
    private String ipLabel;
    private Date createTime;
    private Integer diggCount;
    private Integer replyCommentTotal;
    private String text;
    private Boolean isAuthorDigged;//作者赞过


}
