package com.markyao.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * 配置信息工具类
 */
public class ConfigUtils {

    private final static String fileName="configuraion.properties";
    private final static Properties properties=new Properties();
    static {
        try (FileInputStream fileInputStream = new FileInputStream(ConfigUtils.class.getClassLoader().getResource(fileName).getPath());) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 通过键获取值
     * @param key
     * @return
     */
    public static String getValue(String key){
        return properties.getProperty(key);
    }

    public static void main(String[] args) {
        System.out.println(getValue("db.url"));
        System.out.println(getValue("db.username"));
        System.out.println(getValue("db.password"));
    }
}
