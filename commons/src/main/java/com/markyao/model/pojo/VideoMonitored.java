package com.markyao.model.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("video_monitored")
public class VideoMonitored implements Serializable {
    private Long id;
    private String aid;
    private Date createTime;
    private Integer version;

}
