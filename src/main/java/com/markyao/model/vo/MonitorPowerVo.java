package com.markyao.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MonitorPowerVo {
    private String aid;
    private String cur;
    private VideoInfoVo videoInfoVo;
    private CommentDetailsVo commentDetailsVo;

}
