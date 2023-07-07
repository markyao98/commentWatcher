package com.markyao.model.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class IpModel implements Serializable {

    private String ipLabel;
    private Integer cnt;

    private String div;
}
