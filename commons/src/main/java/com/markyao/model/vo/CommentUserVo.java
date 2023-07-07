package com.markyao.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class CommentUserVo implements Serializable {
    private String id;
    private String uid;
    private String secUid;
    private String avatar;
    private String region;
    private String language;
    private String nickname;
    private Integer userAge;
}
