package com.markyao.model.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@TableName("video_info")
@Accessors(chain = true)
public class VideoInfo implements Serializable {
    private Long id;
    private String awemeId;
    private String watchLink;
    private String titleInfo;
    private String wordCloud;
    private String topWords;
    private Integer totals;
    private Boolean canMonitor;
}
