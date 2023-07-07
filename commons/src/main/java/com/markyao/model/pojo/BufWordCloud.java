package com.markyao.model.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("buf_word_cloud")
public class BufWordCloud implements Serializable {
    private Long id;
    private String wordCloud;
    private String aids;
    private String topWords;
    private Integer totals;
}
