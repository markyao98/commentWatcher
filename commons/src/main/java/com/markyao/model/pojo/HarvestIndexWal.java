package com.markyao.model.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@TableName(" 表: harvest_index_wal")
public class HarvestIndexWal {

    private Long id;
    private Long cid;
    private Integer logType;//1插入 2删除
    private Integer eventStatus;//0初始化 1成功 2失败
    private Integer retryCnt;//重试次数
    private Date createTime;
    private Date updateTime;//更新时间,跟事件状态结合起来看
}
