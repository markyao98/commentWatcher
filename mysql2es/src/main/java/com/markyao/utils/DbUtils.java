package com.markyao.utils;


import java.lang.reflect.Field;
import java.sql.*;

/**
 * 数据库工具类
 */
public class DbUtils {

    /**
     * 获取数据库连接
     * @return
     * @throws SQLException
     */
    public static Connection getConn() throws SQLException {
        String url = ConfigUtils.getValue("db.url");
        String username = ConfigUtils.getValue("db.username");
        String password = ConfigUtils.getValue("db.password");
        Connection connection = DriverManager.getConnection(url, username, password);

        return connection;
    }


    /**
     * 反向映射查询pojo(单个查询)
     * @param sql
     * @param statement
     * @param clazz 需要驼峰命名
     * @param <T>
     * @return
     */
    public static <T>T selectOne(String sql, Statement statement,Class<T> clazz){
        try {
            T t = clazz.newInstance();
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            for (Field field : clazz.getDeclaredFields()) {
                //映射名--> 驼峰规则,需要转换为原列名
                String fieldName = field.getName();
                fieldName=convert(fieldName);
                System.out.println(fieldName);
                //取的类型
                Class<?> fieldType = field.getType();
                Object object = resultSet.getObject(fieldName, fieldType);
                //反向注入成员变量
                field.setAccessible(true);
                field.set(t,object);
            }
            return t;
        } catch (InstantiationException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 驼峰转化至原列名
     * @param camelCase
     * @return
     */
    private static String convert(String camelCase) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        return camelCase.replaceAll(regex, replacement).toLowerCase();
    }

    /**
     * SHOW MASTER STATUS;
     * @return
     */
    public static String getBinlogfileNameAndPosition() {
        String sql="SHOW MASTER STATUS;";
        try (Connection connection=getConn();Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            String binlogFileName = resultSet.getString("File");
            long position = resultSet.getLong("Position");
            return binlogFileName+","+position;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }


    /*public static void main(String[] args) throws SQLException {
        Connection conn = getConn();
        Statement statement = conn.createStatement();
        CommentDetails details = selectOne("select * from harvest_comment_details where id = 54874158668058624", statement, CommentDetails.class);
        statement.close();
        conn.close();
        System.out.println(details);
    }*/

}
