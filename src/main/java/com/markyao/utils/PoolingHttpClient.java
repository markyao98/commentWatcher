package com.markyao.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/*
httpClient工具类,传入url即可获取页面响应内容
 */
@Slf4j
public class PoolingHttpClient {
    private  PoolingHttpClientConnectionManager cm;

    public PoolingHttpClient() {
        //无参构造方法
        this.cm = new PoolingHttpClientConnectionManager();
        //设置配置
        this.cm.setMaxTotal(100);//最大连接数
        this.cm.setDefaultMaxPerRoute(10);//单个主机最大连接数
    }
    public CloseableHttpClient getClient(){
        return HttpClients.custom().setConnectionManager(this.cm).build();
    }
    public String doGetHtml(String url){
        //获取httpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(this.cm).build();

        //创建请求对象,设置url地址
        HttpGet httpGet=new HttpGet(url);
        //发起请求,获取响应
        httpGet.setConfig(this.getConfig());//设置请求信息
        CloseableHttpResponse response =null;
        try {
             response = httpClient.execute(httpGet);
            if(response.getStatusLine().getStatusCode()==200){
                //请求成功
                System.out.println("请求成功------------------");
                HttpEntity entity = response.getEntity();
                //判断响应内容是否为空
                if(entity!=null){
                    String content = EntityUtils.toString(entity,"utf8");//设置编码
                    return content;
                }
                else
                {
                    return "";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private RequestConfig getConfig() {
        RequestConfig config= RequestConfig.custom()
//                .setCookieSpec(CookieSpecs.STANDARD)
                .setConnectTimeout(2000)                //创建连接最长时间,单位毫秒
                .setConnectionRequestTimeout(100)       //设置获取连接最长时间
                .setSocketTimeout(10*1000)              //设置数据传输最长时间
                .build();
        return config;
    }

}
