package com.markyao.dao;

import com.markyao.model.pojo.CommentUser;
import com.markyao.utils.DbUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * JDBC 查询user表
 */
public class CommentUserDao {
    private final static String db_name="harvest_comment_user";

    public CommentUser selectUserById(Long uid) {
        CommentUser commentUser=null;
        Connection conn=null;
        Statement statement=null;
        try {
            conn = DbUtils.getConn();
            statement = conn.createStatement();
            String sql="SELECT * FROM "+db_name+" WHERE ID="+uid;
            commentUser = DbUtils.selectOne(sql, statement, CommentUser.class);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                statement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return commentUser;
    }

}
