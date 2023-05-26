package com.markyao.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserVo {

    private String secUid;

    private Long id;

    private Long did;

    private String text;
}
