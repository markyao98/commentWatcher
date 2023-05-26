package com.markyao.model.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@TableName("video_info")
@Accessors(chain = true)
public class VideoInfo {
    private Long id;
    private String awemeId;
    private String watchLink;
    private String titleInfo;
    private String wordCloud;
    private String topWords;
    private Integer totals;
}
