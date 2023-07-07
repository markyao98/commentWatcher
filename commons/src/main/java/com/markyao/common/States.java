package com.markyao.common;

public class States {
    public static String STOP_HARVEST_COMMENTS="STOP_HARVEST_COMMENTS";
    public static String COMPLETE_HARVEST_COMMENTS="COMPLETE_HARVEST_COMMENTS";
    public static String CUR_HARVEST_COMMENTS_CNT="CUR_HARVEST_COMMENTS_CNT";
    public static int S_COMMON_SEARCH=0;
    public static int S_QUERY_SEARCH=1; //text查询
    public static int S_ID_SEARCH=2;  //id查询
    public static int S_IP_SEARCH=3;  //ip查询
    public static int S_DATE_SEARCH=4;  //时间段查询

    public static String ORDER_COMMON="0";  //普通不排序
    public static String ORDER_IP="1";  //ip排序
    public static String ORDER_DATE="2";  //时间排序
    public static String ORDER_DIGGCOUNT="3";  //点赞排序
    public static String ORDER_REPLYOUNT="4";  //点赞排序

    public static String MONITORING="MONITORING";
    public static String POWER_MONITORING="POWER_MONITORING";

}
