package com.markyao.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class MonitorVo implements Serializable {

    private String aid;
    private String title;
    private int maxChange; //最大点赞的变化量
    private String startDate; //开始监控的时间
}
