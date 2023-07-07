package com.markyao.dao;

import com.markyao.model.pojo.VideoInfo;
import com.markyao.utils.DbUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 查询video
 */
public class VideoDao {
    private final static String db_name="video_info";

    public VideoInfo selectVideoByaId(String aid) {
        VideoInfo videoInfo=null;
        Connection conn=null;
        Statement statement=null;
        try {
            conn = DbUtils.getConn();
            statement = conn.createStatement();
            String sql="SELECT * FROM "+db_name+" WHERE aweme_id="+aid;
            videoInfo = DbUtils.selectOne(sql, statement, VideoInfo.class);
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

        return videoInfo;
    }
}
