package com.markyao.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageVo implements Serializable {
    private long totals;
    private long totalPages;
    private Object list;
    private Object info;


}
