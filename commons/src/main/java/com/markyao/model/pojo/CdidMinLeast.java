package com.markyao.model.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors
public class CdidMinLeast implements Serializable {
    private Long cdid;
    private Date createTime;
}
