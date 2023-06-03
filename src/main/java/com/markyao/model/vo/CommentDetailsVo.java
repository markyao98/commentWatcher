package com.markyao.model.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CommentDetailsVo {
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
