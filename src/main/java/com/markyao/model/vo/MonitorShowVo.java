package com.markyao.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class MonitorShowVo {
    private List<Object>timeData;

    private List<Object>likeData;

    private List<Object>replyData;
}
