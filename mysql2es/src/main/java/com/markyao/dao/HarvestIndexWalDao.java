package com.markyao.dao;

import com.markyao.model.pojo.HarvestIndexWal;
import com.markyao.utils.DbUtils;
import com.markyao.utils.SnowflakeIdGenerator;

import java.sql.*;

/**
 * WAL工具类
 */
public class HarvestIndexWalDao {
    private final static String db_name="harvest_index_wal";
    private final static SnowflakeIdGenerator idGenerator=new SnowflakeIdGenerator(1,1);
/*
    public static void main(String[] args) {
        HarvestIndexWalDao walDao=new HarvestIndexWalDao();
        HarvestIndexWal wal=new HarvestIndexWal();
        long generateId = idGenerator.generateId();
        wal.setRetryCnt(0).setEventStatus(0).setCid(0L).setCreateTime(new java.util.Date())
                .setUpdateTime(new java.util.Date()).setLogType(1).setId(generateId);
        walDao.save(wal);
        System.out.println("插入成功");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        walDao.updateStatus(wal,1);
        System.out.println("更新成功");
    }*/
    /**
     * 插入
     * @param harvestIndexWal
     */
    public void save(HarvestIndexWal harvestIndexWal){

        Connection conn = null;
        PreparedStatement statement=null;
        try {
            conn = DbUtils.getConn();
            String sql="INSERT INTO "+db_name+"(id,cid,log_type,event_status,retry_cnt,create_time,update_time) " +
                    "VALUES (?,?,?,?,?,?,?)";
            statement = conn.prepareStatement(sql);
            statement.setLong(1,harvestIndexWal.getId());
            statement.setLong(2,harvestIndexWal.getCid());
            statement.setInt(3,harvestIndexWal.getLogType());
            statement.setInt(4,harvestIndexWal.getEventStatus());
            statement.setInt(5,harvestIndexWal.getRetryCnt());
            statement.setTimestamp(6,new Timestamp(harvestIndexWal.getCreateTime().getTime()));
            statement.setTimestamp(7,new Timestamp(harvestIndexWal.getUpdateTime().getTime()));
            boolean execute = statement.execute();

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
    }



    /**
     *
     * @param harvestIndexWallog
     * @param status
     * 1成功 2失败
     */
    public void updateStatus(HarvestIndexWal harvestIndexWallog, int status) {
        Connection conn = null;
        PreparedStatement statement=null;
        try {
            conn = DbUtils.getConn();
            String sql="UPDATE "+db_name+" SET event_status = ?,update_time = ? WHERE id=?";
            statement = conn.prepareStatement(sql);
            statement.setInt(1,status);
            statement.setTimestamp(2,new Timestamp(System.currentTimeMillis()));
            statement.setLong(3,harvestIndexWallog.getId());

            statement.execute();
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
    }
}
