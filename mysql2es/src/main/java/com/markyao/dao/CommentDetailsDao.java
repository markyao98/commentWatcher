package com.markyao.dao;

import com.markyao.model.pojo.CommentDetails;
import com.markyao.utils.DbUtils;

import java.sql.*;

/**
 * JDBC 查询details表
 */
public class CommentDetailsDao {
    private final static String db_name="harvest_comment_details";

    public CommentDetails selectDetailsById(Long did){
        CommentDetails commentDetails=null;
        Connection conn = null;
        Statement statement=null;
        try {
            conn = DbUtils.getConn();
            statement = conn.createStatement();
            String sql = "SELECT * FROM "+db_name+" where id="+did;
            commentDetails = DbUtils.selectOne(sql, statement, CommentDetails.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                statement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return commentDetails;
    }

    private void oldQuery(Long did, CommentDetails commentDetails, ResultSet resultSet) throws SQLException {
        String aid = resultSet.getString("aweme_id");
        String cid = resultSet.getString("cid");
        String ipLabel = resultSet.getString("ip_label");
        Date createTime = resultSet.getDate("create_time");
        int digg_count = resultSet.getInt("digg_count");
        int reply_comment_total = resultSet.getInt("reply_comment_total");
        String text = resultSet.getString("text");
        Boolean is_author_digged = resultSet.getBoolean("is_author_digged");
        int cur = resultSet.getInt("cur");
        int count = resultSet.getInt("count");
        // 处理每一行的数据
        commentDetails.setId(did)
                .setAwemeId(aid)
                .setCid(cid)
                .setIpLabel(ipLabel)
                .setCreateTime(createTime)
                .setDiggCount(digg_count)
                .setReplyCommentTotal(reply_comment_total)
                .setText(text)
                .setIsAuthorDigged(is_author_digged)
                .setCur(cur)
                .setCount(count);
    }
}
