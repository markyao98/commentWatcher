package com.markyao.model.dto;

import com.markyao.model.pojo.CommentUser;
import com.markyao.model.vo.CommentDetailsVo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class EsCommentDto implements Serializable {
    private Long id;

    private Long userId;
    private String userSecUid;
    private String userAvatar;
    private String userRegion;
    private String userLanguage;
    private String userNickname;
    private Integer userUserAge;

    private Long detailId;
    private String detailAwemeId;
    private String detailCid;
    private String detailIpLabel;
    private Date detailCreateTime;
    private Integer detailDiggCount;
    private Integer detailReplyCommentTotal;
    private String detailText;
    private Boolean detailIsAuthorDigged;//作者赞过
    private Integer detailCur;
    private Integer detailCount;

    private String videoTitle;
    private String userCardLink;
    private Boolean isMonitored; //是否被监控过
}
