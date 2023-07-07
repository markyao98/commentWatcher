package com.markyao.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtils {

    public static String getFormat(Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
        return sdf.format(date);
    }
    public static String getFormat(String format){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");

        try {
            return sdf.format(sdf.parse(format));
        } catch (ParseException e) {
            e.printStackTrace();
            return format;
        }
    }
    public static String getFormatCustom(String format){
        if (isNanos(format)){
            return getFormat(Long.valueOf(format));
        }
        StringBuilder sb=new StringBuilder();
        char[] chars = format.toCharArray();
        for (char c : chars) {
            if (c=='T' || c=='Z'){
                sb.append(" ");
            }else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static boolean isNanos(String format) {
        for (char c : format.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public static String getFormat(Long nanos){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=new Date(nanos);
        return sdf.format(date);
    }
    public static void main(String[] args) {
//        String strd="Fri Jun 09 19:01:11 CST 2023";
//        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
//        sdf.parse(strd);
//        Date date=new Date()
    }

}
