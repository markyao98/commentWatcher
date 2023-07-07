package test;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BinlogParserExample {
    public static void main(String[] args) throws Exception {
        String hostname = "localhost";
        int port = 3306;
        String username = "root";
        String password = "root";

        String binlogFilename = "mysql-bin.000235";
        long binlogPosition = 3041;

        BinaryLogClient client = new BinaryLogClient(hostname, port, username, password);

        Map<Long,String>tableIdMap=new ConcurrentHashMap<>();

        // 设置起始的 binlog 文件和位置
        client.setBinlogFilename(binlogFilename);
        client.setBinlogPosition(binlogPosition);
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
                    System.out.println("放入map..");
                    tableIdMap.put(eventData.getTableId(),eventData.getTable());
                }
            }
            // 处理插入事件
            if (data instanceof WriteRowsEventData) {
                WriteRowsEventData eventData = (WriteRowsEventData) data;
                System.out.println("表Id: " + eventData.getTableId());
                List<Serializable[]> rows = eventData.getRows();
                System.out.println("插入行: " + rows);
                for (Serializable[] row : rows) {
                    for (Serializable c : row) {
                        System.out.print(c+" , ");
                    }
                    System.out.println();
                }
            }
            // 处理更新事件
            if (data instanceof UpdateRowsEventData) {
                UpdateRowsEventData eventData = (UpdateRowsEventData) data;
                System.out.println("表Id: " + eventData.getTableId());
                List<Map.Entry<Serializable[], Serializable[]>> rows1 = eventData.getRows();
                System.out.println("更新行: " + rows1);
                for (Map.Entry<Serializable[], Serializable[]> entry : rows1) {
                    Serializable[] befores = entry.getKey();
                    Serializable[] after = entry.getValue();
                    System.out.println("更新前============>");
                    for (Serializable s : befores) {
                        System.out.print(s+" , ");
                    }
                    System.out.println("更新后============>");
                    for (Serializable s : after) {
                        System.out.print(s+" , ");
                    }
                    System.out.println();
                }
            }
            //处理删除事件
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

        // 连接到 MySQL 服务器并开始解析 binlog 事件
        client.connect();

        // 保持程序运行
        Thread.sleep(Long.MAX_VALUE);

        // 断开与 MySQL 服务器的连接
        client.disconnect();
    }
}
