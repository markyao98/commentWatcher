package com.markyao.utils;

import com.markyao.config.IDgeneratorConfig;
import com.markyao.model.pojo.CommentDetails;
import com.markyao.model.pojo.CommentUser;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FillUtils {
    private final static SnowflakeIdGenerator idGenerator=new SnowflakeIdGenerator(1,1);

    public static  CommentDetails fillCommentDetails(Map map) {
        String digg_count = map.get("digg_count").toString();
        String ip_label = null;
        try {
            ip_label = map.get("ip_label").toString();
        } catch (Exception e) {
            ip_label="未知IP";
        }

        String reply_comment_total = map.get("reply_comment_total").toString();
        String text = map.get("text").toString();
//        text=unicoderString(text);

        BigInteger create_time0 = (BigInteger) map.get("create_time");
//        long create_times=targetTime(create_time0.longValue());

        String aweme_id = map.get("aweme_id").toString();
        String cid = map.get("cid").toString();
        Boolean is_author_digged = (Boolean) map.get("is_author_digged");
        CommentDetails commentDetails=new CommentDetails();
        commentDetails
                .setId(idGenerator.generateId())
                .setCid(cid).setAwemeId(aweme_id).setIpLabel(ip_label)
                .setCreateTime(new Date(create_time0.longValue()*1000L)).setDiggCount(Integer.valueOf(digg_count))
                .setReplyCommentTotal(Integer.valueOf(reply_comment_total)).setText(text).setIsAuthorDigged(is_author_digged);
        return commentDetails;
    }


    public static CommentUser fillCommentUser(Map map) {
        try {
            Map<String,Object> user = (Map) map.get("user");
            String uid = user.get("uid").toString();
            String sec_uid = user.get("sec_uid").toString();

            Map<String,Object> avatar_168x168 = (Map) user.get("avatar_168x168");
            List url_list = (List) avatar_168x168.get("url_list");
            String avatar = url_list.get(0).toString();

            String region = user.get("region").toString();

            String language = user.get("language").toString();
            String nickname = user.get("nickname").toString();
//            nickname=unicoderString(nickname);
//            log.debug("处理后的nickname {}",nickname);

            String user_age = user.get("user_age").toString();

            CommentUser commentUser=new CommentUser();
            commentUser
                    .setId(idGenerator.generateId())
                    .setAvatar(avatar).setUserAge(Integer.valueOf(user_age)).setLanguage(language).setNickname(nickname).setSecUid(sec_uid)
                    .setUid(uid).setRegion(region);
            return commentUser;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new CommentUser();
    }



}
