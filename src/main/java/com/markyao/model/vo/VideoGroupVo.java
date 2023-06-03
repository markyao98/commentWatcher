package com.markyao.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class VideoGroupVo {
    private String id;
    private String title;
    private String descMsg;
    private String img;


    private Date createTime;

    private Date updateTime;

}
