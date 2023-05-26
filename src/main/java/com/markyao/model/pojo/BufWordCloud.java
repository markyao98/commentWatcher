package com.markyao.model.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("buf_word_cloud")
public class BufWordCloud {
    private Long id;
    private String wordCloud;
    private String aids;
    private String topWords;
    private Integer totals;
}
