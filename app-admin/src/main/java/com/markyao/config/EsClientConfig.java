package com.markyao.config;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsClientConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        RestHighLevelClient client=null;
        final BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
        if (client == null) {
            client = new RestHighLevelClient(
                    RestClient.builder(new HttpHost("localhost", 9200, "http"))
                            .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                                @Override
                                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                                    httpClientBuilder.disableAuthCaching();
                                    return httpClientBuilder.setDefaultCredentialsProvider(basicCredentialsProvider);
                                }
                            })
            );
        }
        return client;
    }
}
