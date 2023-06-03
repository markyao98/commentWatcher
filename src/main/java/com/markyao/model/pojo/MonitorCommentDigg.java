package com.markyao.model.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@TableName("monitor_comment_digg")
@Accessors(chain = true)
public class MonitorCommentDigg {

    private Long id;
    private Long cdid;
    private Integer digCount;
    private Integer replyCommentTotal;
    private Boolean isAuthorDigged;
    private Date createTime;
}
