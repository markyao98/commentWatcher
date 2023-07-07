package com.markyao.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class VideoInfoVo implements Serializable {
    private String id;
    private String awemeId;
    private String watchLink;
    private String titleInfo;

}
