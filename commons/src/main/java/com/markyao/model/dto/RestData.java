package com.markyao.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class RestData implements Serializable {
    private Boolean success;
    private Integer code;
    private String msg;
    private Object data;

    public static RestData success(Object data){
        return new RestData(true,200,"成功",data);
    }

    public static RestData fails(String msg){
        return new RestData(false,500,"失败",null);
    }
}
