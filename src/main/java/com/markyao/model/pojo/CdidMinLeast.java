package com.markyao.model.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors
public class CdidMinLeast {
    private Long cdid;
    private Date createTime;
}
