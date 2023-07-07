package com.markyao.model.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@TableName("harvest_comment_user")
@Accessors(chain = true)
public class CommentUser implements Serializable {
    private Long id;
    private String uid;
    private String secUid;
    private String avatar;
    private String region;
    private String language;
    private String nickname;
    private Integer userAge;

}
