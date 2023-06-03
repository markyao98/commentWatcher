package com.markyao.utils;

public class MonitorPowerUtils {
    public static final String split_reg="::";
    public static String getAid(String key){
        return key.split(split_reg)[0];
    }
    public static String getCur(String key){
        return key.split(split_reg)[1];
    }
    public static String getDid(String key){
        return key.split(split_reg)[2];
    }
}
