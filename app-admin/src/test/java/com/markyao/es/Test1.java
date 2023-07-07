package com.markyao.es;

import com.markyao.es.utils.EsUtils;
import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
@Slf4j
public class Test1 {
    @Autowired
    RestHighLevelClient client;
    @Value("${es.index_1}")
    String indexName;
    @Test
    void t1(){
        RestHighLevelClient restHighLevelClient=null;
        final BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
        if (restHighLevelClient == null) {
            restHighLevelClient = new RestHighLevelClient(
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
        String field="detailText";
        String searchVal="日本";
        //构建搜索请求
        SearchRequest searchRequest=new SearchRequest(indexName);
        SearchSourceBuilder sourceBuilder=new SearchSourceBuilder();

        sourceBuilder.query(QueryBuilders.matchQuery(field,searchVal).fuzziness(Fuzziness.AUTO));

        sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
        searchRequest.source(sourceBuilder);

        //执行搜索
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            //处理结果
            SearchHit[] hits = response.getHits().getHits();
            for (SearchHit hit : hits) {
                //处理每个搜索结果
                String id = hit.getId();
                float score = hit.getScore();
                String source = hit.getSourceAsString();
                log.info("id: {} ,score： {}",id,score);
                log.info("source: {}",source);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            restHighLevelClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    void t2(){
        RestHighLevelClient client = EsUtils.getClient();
        String termField="detailAwemeId";
        String termVal1="7169359016567541029";

        String searchField="detailText";
        String searchVal="日本";
        //构建搜索请求
        SearchRequest searchRequest=new SearchRequest(indexName);
        SearchSourceBuilder sourceBuilder=new SearchSourceBuilder();

        //构建term查询(bool查询)
        BoolQueryBuilder boolQuery=QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery(termField,termVal1));
        //构建模糊查询
        boolQuery.should(QueryBuilders.matchQuery(searchField,searchVal).fuzziness(Fuzziness.AUTO));

        sourceBuilder.query(boolQuery);
        sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
        searchRequest.source(sourceBuilder);

        //执行搜索
        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

            //处理结果
            SearchHit[] hits = response.getHits().getHits();
            for (SearchHit hit : hits) {
                //处理每个搜索结果
                String id = hit.getId();
                float score = hit.getScore();
                String source = hit.getSourceAsString();
                log.info("id: {} ,score： {}",id,score);
                log.info("source: {}",source);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
