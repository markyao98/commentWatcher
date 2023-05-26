package com.markyao.model.vo;

import lombok.Data;

@Data
public class PageVo {
    private long totals;
    private long totalPages;
    private Object list;
    private Object info;


}
