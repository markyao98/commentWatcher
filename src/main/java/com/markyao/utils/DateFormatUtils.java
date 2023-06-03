package com.markyao.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtils {

    public static String getFormat(Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
        return sdf.format(date);
    }
}
