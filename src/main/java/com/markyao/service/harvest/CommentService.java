package com.markyao.service.harvest;

import com.baomidou.mybatisplus.extension.service.IService;
import com.markyao.model.dto.RestData;
import com.markyao.model.pojo.Comment;
import com.markyao.model.pojo.HarvestCommentUrl;
import org.apache.tomcat.util.json.ParseException;

import java.io.IOException;

public interface CommentService extends IService<Comment> {
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


    RestData pages(int current, int pageSize);
    RestData pages(String awemeId,int current, int pageSize,int searchType,Object searchMsg,String sortType) ;

    void startHarvestAllComments();

    void harvestComments(String url, HarvestCommentUrl c)throws IOException, ParseException;


}
