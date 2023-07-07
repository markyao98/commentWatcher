package com.markyao.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class MonitorPowerVo implements Serializable {
    private String aid;
    private String cur;
    private VideoInfoVo videoInfoVo;
    private CommentDetailsVo commentDetailsVo;

}
