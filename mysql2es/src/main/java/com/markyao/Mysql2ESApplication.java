package com.markyao;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import com.markyao.utils.ConfigUtils;
import com.markyao.utils.DbUtils;
import com.markyao.model.pojo.Comment;
import com.markyao.service.EsStorageService;
import com.markyao.service.impl.EsStorageServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MySQL同步至Es程序
 * 目前已完成增量同步.
 */
@Slf4j
public class Mysql2ESApplication {
    private final static String hostname = ConfigUtils.getValue("db.host");
    private final static int port = Integer.parseInt(ConfigUtils.getValue("db.port"));
    private final static String username = ConfigUtils.getValue("db.username");
    private final static String password = ConfigUtils.getValue("db.password");
    private final static String tb1="harvest_comment";

    private final static EsStorageService esStorageService=new EsStorageServiceImpl();


    public static void main(String[] args) {
        start0();
    }


    private static void start0() {
        try {
            BinaryLogClient client = getBinlogClient();
            log.info("binlog客户端初始化完毕~");
            Map<Long,String> tableIdMap=new ConcurrentHashMap<>();
            // 注册事件监听器
            client.registerEventListener(event -> {
                EventData data = event.getData();
                //滚动日志文件事件，表示写入了新的日志文件，需要更新日志文件名.
                if (data instanceof RotateEventData){
                    RotateEventData eventData=(RotateEventData)data;
                    String newBigLogname = eventData.getBinlogFilename();
                    long newBinlogPosition = eventData.getBinlogPosition();
                    // 设置起始的 binlog 文件和位置
                    client.setBinlogFilename(newBigLogname);
                    client.setBinlogPosition(newBinlogPosition);
                }
                //tableMap事件
                if (data instanceof TableMapEventData){
                    TableMapEventData eventData=(TableMapEventData)data;
                    if (!tableIdMap.containsKey(eventData.getTableId())){
                        System.out.println("表id: "+eventData.getTableId()+" 映射表名: "+eventData.getTable());
                        log.info("放入Map..");
                        tableIdMap.put(eventData.getTableId(),eventData.getTable());
                    }
                }
                // 处理插入事件
                if (data instanceof WriteRowsEventData) {
                    WriteRowsEventData eventData = (WriteRowsEventData) data;
                    long tableId = eventData.getTableId();
                    if (tableIdMap.containsKey(tableId) && tableIdMap.get(tableId).equals(tb1)){
                        log.info("监测到comment表,需要新增至es");
                        long l = System.currentTimeMillis();
                        Comment comment=getComment(eventData);
                        //WAL
                        esStorageService.save(comment);
                        log.info("插入成功!! 耗时: {}",(System.currentTimeMillis()-l));
                    }
                }
                //TODO 处理更新事件
                if (data instanceof UpdateRowsEventData) {
                    UpdateRowsEventData eventData = (UpdateRowsEventData) data;
                    System.out.println("表Id: " + eventData.getTableId());
                    List<Map.Entry<Serializable[], Serializable[]>> rows1 = eventData.getRows();
                    System.out.println("更新行: " + rows1);
                    for (Map.Entry<Serializable[], Serializable[]> entry : rows1) {
                        Serializable[] befores = entry.getKey();
                        Serializable[] after = entry.getValue();
//                        System.out.println("更新前============>");
                        for (Serializable s : befores) {
                            System.out.print(s+" , ");
                        }
//                        System.out.println("更新后============>");
                        for (Serializable s : after) {
                            System.out.print(s+" , ");
                        }
                        System.out.println();
                    }
                }
                //TODO 处理删除事件
                if (data instanceof DeleteRowsEventData) {
                    DeleteRowsEventData eventData = (DeleteRowsEventData) data;
                    System.out.println("表Id: " + eventData.getTableId());
                    List<Serializable[]> rows = eventData.getRows();
                    System.out.println("删除行: " + rows);
                    for (Serializable[] row : rows) {
                        for (Serializable s : row) {
                            System.out.print(s+" , ");
                        }
                        System.out.println();
                    }
                }
            });

            log.info("注册事件监听器成功~");
            log.info("连接到 MySQL 服务器并开始解析 binlog 事件~");
            client.connect();
            // 保持程序运行
            Thread.sleep(Long.MAX_VALUE);
            // 断开与 MySQL 服务器的连接
            client.disconnect();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }

    /**
     * 获取binlog客户端
     * @return
     */
    private static BinaryLogClient getBinlogClient(){
        //获取最新的binlog文件以及position
        String str=DbUtils.getBinlogfileNameAndPosition();
        String binlogFilename = str.split(",")[0];
        long binlogPosition = Long.parseLong(str.split(",")[1]);
//        long binlogPosition = 3041;
        BinaryLogClient client = new BinaryLogClient(hostname, port, username, password);
        // 设置起始的 binlog 文件和位置
        client.setBinlogFilename(binlogFilename);
        client.setBinlogPosition(binlogPosition);
        return client;
    }
    /**
     * 通过binlog解析出的数据获取Comment
     * Comment --> id uid aid did create_time update_time
     * @param eventData
     * @return
     */
    private static Comment getComment(WriteRowsEventData eventData) {
        Comment comment=new Comment();
        //获取行
        List<Serializable[]> rows = eventData.getRows();
        //遍历每一行
        for (Serializable[] row : rows) {
            Long id = (Long) row[0];
            Long uid = (Long) row[1];
            Serializable aid = row[2];
            Long did = (Long) row[3];
            Date create_time = (Date) row[4];
            Date update_time = (Date) row[5];

            comment.setId(id)
                    .setUid(uid)
                    .setAid(aid.toString())
                    .setDid(did)
                    .setCreateTime(create_time)
                    .setUpdateTime(update_time);
        }
        return comment;
    }
}
