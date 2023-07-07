package com.markyao.es.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.markyao.asnc.ThreadService;
import com.markyao.common.States;
import com.markyao.es.CommentEsService;
import com.markyao.es.utils.EsUtils;
import com.markyao.mapper.MonitorCommentDiggMapper;
import com.markyao.mapper.VideoInfoMapper;
import com.markyao.model.dto.RestData;
import com.markyao.model.pojo.Comment;
import com.markyao.model.pojo.CommentUser;
import com.markyao.model.pojo.MonitorCommentDigg;
import com.markyao.model.pojo.VideoInfo;
import com.markyao.model.vo.*;
import com.markyao.service.VideoInfoService;
import com.markyao.utils.DateFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.markyao.common.States.*;

@Slf4j
@Component
public class CommentEsServiceImpl implements CommentEsService {
    @Value("${es.index_1}")
    String indexName;
    @Value("${douyin.user.cardPrefix}")
    String usercardPrefix;
    @Autowired
    MonitorCommentDiggMapper monitorCommentDiggMapper;
    @Autowired
    VideoInfoService videoInfoService;
    @Autowired
    ThreadService threadService;
    @Autowired
    VideoInfoMapper videoInfoMapper;
    /**
     * 根据不同的排序类型，不同的搜索类型进行聚合搜索
     * 当搜索评论内容的时候,使用fuzzy模糊搜索
     * @param awemeId
     * @param current
     * @param pageSize
     * @param searchType
     * @param searchVal
     * @param sortType
     * @return
     */
    @Override
    public RestData pages(String awemeId, int current, int pageSize, int searchType, Object searchVal, String sortType,int sortRet) {
        RestHighLevelClient client = EsUtils.getClient();

        //构建搜索请求
        SearchRequest searchRequest=new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        VideoInfoVo videoInfoVo=null;
        if (!StringUtils.isEmpty(awemeId)&&!"null".equals(awemeId)) {
            videoInfoVo=getVideoInfo(awemeId);
        }
        //构建query
        QueryBuilder query=getQuery(awemeId,searchType,searchVal);
        searchSourceBuilder.query(query);

        //排序处理
        sortBySortType(searchSourceBuilder,sortType,sortRet==1?SortOrder.ASC:SortOrder.DESC);
        searchSourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));

        //分页处理
        searchSourceBuilder.from((current-1)*pageSize).size(pageSize);

        //高亮处理
        highLightBySearchType(searchSourceBuilder,searchType);
        searchSourceBuilder.trackTotalHits(true);
        searchRequest.source(searchSourceBuilder);
        //获取准确总数
        searchRequest.searchType(SearchType.QUERY_THEN_FETCH);
        //执行搜索
        long totals=-1;
        List<CommentVo> collect=new ArrayList<>(pageSize);
        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            log.info("es请求:{}",searchRequest.source().toString());
            //处理结果
            SearchHit[] hits = response.getHits().getHits();
            totals=response.getHits().getTotalHits().value;
            threadService.updateVideoCommentsTotals(totals,awemeId,videoInfoMapper);
            for (SearchHit hit : hits) {
                //处理每个搜索结果
                String source = hit.getSourceAsString();
//                CommentVo commentVo=getCommentVoBySource(source);
                CommentVo commentVo=getCommentVoByHit(hit);

                collect.add(commentVo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



        PageVo pageVo=new PageVo();
        pageVo.setList(collect);
        pageVo.setTotals(totals);
        pageVo.setTotalPages(totals/pageSize);
        pageVo.setInfo(videoInfoVo);
        return RestData.success(pageVo);
    }

    @Override
    public RestData pages(String[] awemeIds, int current, int pageSize, int searchType, Object searchMsg, String sortType, int sortRet) {
        RestHighLevelClient client = EsUtils.getClient();

        //构建搜索请求
        SearchRequest searchRequest=new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        VideoInfoVo videoInfoVo=null;

        //构建query
        QueryBuilder query=getQuery(awemeIds,searchType,searchMsg);
        searchSourceBuilder.query(query);

        //排序处理
        sortBySortType(searchSourceBuilder,sortType,sortRet==1?SortOrder.ASC:SortOrder.DESC);
        searchSourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));

        //分页处理
        searchSourceBuilder.from((current-1)*pageSize).size(pageSize);

        //高亮处理
        highLightBySearchType(searchSourceBuilder,searchType);
        searchSourceBuilder.trackTotalHits(true);
        searchRequest.source(searchSourceBuilder);
        //获取准确总数
        searchRequest.searchType(SearchType.QUERY_THEN_FETCH);
        //执行搜索
        long totals=-1;
        List<CommentVo> collect=new ArrayList<>(pageSize);
        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            log.info("es请求:{}",searchRequest.source().toString());
            //处理结果
            SearchHit[] hits = response.getHits().getHits();
            totals=response.getHits().getTotalHits().value;

            for (SearchHit hit : hits) {
                //处理每个搜索结果
                String source = hit.getSourceAsString();
//                CommentVo commentVo=getCommentVoBySource(source);
                CommentVo commentVo=getCommentVoByHit(hit);

                collect.add(commentVo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



        PageVo pageVo=new PageVo();
        pageVo.setList(collect);
        pageVo.setTotals(totals);
        pageVo.setTotalPages(totals/pageSize);
        pageVo.setInfo(videoInfoVo);
        return RestData.success(pageVo);
    }

    private CommentVo getCommentVoByHit(SearchHit hit) {
        String source = hit.getSourceAsString();
        CommentVo commentVo=new CommentVo();
        Map mp = JSON.parseObject(source, Map.class);
        CommentUserVo commentUser = new CommentUserVo();
        commentUser
                .setId(mp.get("userId").toString())
                .setSecUid((String) mp.get("userSecUid"))
                .setAvatar((String) mp.get("userAvatar"))
                .setLanguage((String) mp.get("userLanguage"))
                .setRegion((String) mp.get("userRegion"))
                .setNickname((String) mp.get("userNickname"))
                .setUserAge((Integer) mp.get("userUserAge"));

        CommentDetailsVo commentDetailsVo=new CommentDetailsVo();
        commentDetailsVo
                .setId(mp.get("detailId").toString())
                .setAwemeId((String) mp.get("detailAwemeId"))
                .setCid((String) mp.get("detailCid"))
                .setIpLabel((String) mp.get("detailIpLabel"))
                .setCreateTime(DateFormatUtils.getFormatCustom(mp.get("detailCreateTime").toString()))
                .setDiggCount((Integer) mp.get("detailDiggCount"))
                .setReplyCommentTotal((Integer) mp.get("detailReplyCommentTotal"))
                .setText((String) mp.get("detailText"))
                .setVideoTitle((String) mp.get("videoTitle"))
                .setIsAuthorDigged((Boolean) mp.get("detailIsAuthorDigged"))
                .setCur((Integer) mp.get("detailCur"))
                .setCount((Integer) mp.get("detailCount"));

        commentVo.setId((Long) mp.get("id"));
        commentVo.setCommentUserVo(commentUser);
        commentVo.setUserCardLink(usercardPrefix+commentUser.getSecUid());

        try {
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            StringBuilder hltext=new StringBuilder();
            Arrays.stream(highlightFields.get("detailText").getFragments()).forEach(s->hltext.append(s));
            commentDetailsVo.setText(hltext.toString());
        } catch (Exception e) {
            commentDetailsVo.setText((String) mp.get("detailText"));
        }
        commentVo.setCommentDetails(commentDetailsVo);
        int cnt;
        try {
            cnt = monitorCommentDiggMapper.selectCount(new QueryWrapper<MonitorCommentDigg>().eq("cdid", commentDetailsVo.getId()));
        } catch (Exception e) {cnt=-1;}
        if (cnt!=-1 && cnt>0){
            commentVo.setIsMonitored(true);
        }else {
            commentVo.setIsMonitored(false);
        }
        return commentVo;
    }

    /**
     * 高亮处理,针对评论内容以及IP
     * @param searchSourceBuilder
     * @param searchType
     */
    private void highLightBySearchType(SearchSourceBuilder searchSourceBuilder, int searchType) {
        if (searchType==S_QUERY_SEARCH){
            searchSourceBuilder.highlighter(new HighlightBuilder()
                    .field("detailText")
                    .preTags("<span class=\"highlight\">")
                    .postTags("</span>"));
        }else if (searchType==S_IP_SEARCH){
            searchSourceBuilder.highlighter(new HighlightBuilder()
                    .field("detailIpLabel")
                    .preTags("<span class=\"highlight\">")
                    .postTags("</span>"));
        }

    }

    /**
     * sourceString -> CommentVo
     * @param source
     * @return
     */
    private CommentVo getCommentVoBySource(String source) {
        CommentVo commentVo=new CommentVo();
        Map mp = JSON.parseObject(source, Map.class);
        CommentUserVo commentUser = new CommentUserVo();
        commentUser
                .setId(mp.get("userId").toString())
                .setSecUid((String) mp.get("userSecUid"))
                .setAvatar((String) mp.get("userAvatar"))
                .setLanguage((String) mp.get("userLanguage"))
                .setRegion((String) mp.get("userRegion"))
                .setNickname((String) mp.get("userNickname"))
                .setUserAge((Integer) mp.get("userUserAge"));

        CommentDetailsVo commentDetailsVo=new CommentDetailsVo();
        commentDetailsVo
                .setId(mp.get("detailId").toString())
                .setAwemeId((String) mp.get("detailAwemeId"))
                .setCid((String) mp.get("detailCid"))
                .setIpLabel((String) mp.get("detailIpLabel"))
                .setCreateTime(DateFormatUtils.getFormatCustom(mp.get("detailCreateTime").toString()))
                .setDiggCount((Integer) mp.get("detailDiggCount"))
                .setReplyCommentTotal((Integer) mp.get("detailReplyCommentTotal"))
                .setText((String) mp.get("detailText"))
                .setVideoTitle((String) mp.get("videoTitle"))
                .setIsAuthorDigged((Boolean) mp.get("detailIsAuthorDigged"))
                .setCur((Integer) mp.get("detailCur"))
                .setCount((Integer) mp.get("detailCount"));

        commentVo.setId((Long) mp.get("id"));
        commentVo.setCommentUserVo(commentUser);
        commentVo.setCommentDetails(commentDetailsVo);
        commentVo.setUserCardLink(usercardPrefix+commentUser.getSecUid());
        int cnt;
        try {
            cnt = monitorCommentDiggMapper.selectCount(new QueryWrapper<MonitorCommentDigg>().eq("cdid", commentDetailsVo.getId()));
        } catch (Exception e) {cnt=-1;}
        if (cnt!=-1 && cnt>0){
            commentVo.setIsMonitored(true);
        }else {
            commentVo.setIsMonitored(false);
        }
        return commentVo;
    }


    private VideoInfoVo getVideoInfo(String awemeId) {
        VideoInfo videoInfo = videoInfoService.getOne(new QueryWrapper<VideoInfo>().eq("aweme_id", awemeId));
        VideoInfoVo videoInfoVo = new VideoInfoVo();
        BeanUtils.copyProperties(videoInfo,videoInfoVo);
        videoInfoVo.setId(videoInfo.getId()+"");
        return videoInfoVo;
    }

    /**
     * 排序规则
     * @param sourceBuilder
     * @param sortType
     * @param sortOrder
     */
    private void sortBySortType(SearchSourceBuilder sourceBuilder, String sortType,SortOrder sortOrder) {
        String fieldName;
        if (sortType.equals(ORDER_DATE)){
            //根据日期排序
            fieldName="detailCreateTime";
        }else if (sortType.equals(ORDER_DIGGCOUNT)){
            fieldName="detailDiggCount";
        }else if (sortType.equals(ORDER_IP)){
            fieldName="detailIpLabel";
        }else if (sortType.equals(ORDER_REPLYOUNT)){
            fieldName="detailReplyCommentTotal";
        }else {
            return;
        }
        sourceBuilder.sort(fieldName,sortOrder);
    }

    /**
     * 构建检索条件——单一条件检索 / aid+条件检索
     * @param awemeId
     * @param searchType
     * @param searchVal
     * @return
     */
    private QueryBuilder getQuery(String awemeId, int searchType, Object searchVal) {
        BoolQueryBuilder queryBuilder=QueryBuilders.boolQuery();
        if (!StringUtils.isEmpty(awemeId)&& !"null".equals(awemeId)) {
            String termField="detailAwemeId";
            String termVal1=awemeId;
            queryBuilder.must(QueryBuilders.termQuery(termField,termVal1));
        }
        if (searchType==S_IP_SEARCH){
            queryBuilder.must()
                    .add(QueryBuilders.termQuery("detailIpLabel",searchVal));
        }
        else if (searchType==S_QUERY_SEARCH){
            queryBuilder.must()
                    .add(QueryBuilders.matchQuery("detailText",searchVal)
                            .fuzziness(Fuzziness.AUTO));
        }
        else if (searchType== S_DATE_SEARCH){
            String dateStr = searchVal.toString();
            String startDateTimeStr = dateStr.toString()+":00";
            String eneDateTimeStr = dateStr.toString()+":59";
            queryBuilder.must()
                    .add(QueryBuilders.rangeQuery("detailCreateTime")
                            .gte(startDateTimeStr)
                            .lte(eneDateTimeStr));
        }

        return queryBuilder;
    }
    private QueryBuilder getQuery(String[] awemeIds, int searchType, Object searchVal) {
        BoolQueryBuilder queryBuilder=QueryBuilders.boolQuery();
        if (awemeIds!=null && awemeIds.length!=0){
            BoolQueryBuilder queryBuilder1=QueryBuilders.boolQuery();
            String termField="detailAwemeId";
            for (String awemeId : awemeIds) {
                queryBuilder1.should(QueryBuilders.termQuery(termField,awemeId));
            }
            queryBuilder.must(queryBuilder1);
        }
        if (searchType==S_IP_SEARCH){
            queryBuilder.must()
                    .add(QueryBuilders.termQuery("detailIpLabel",searchVal));
        }
        else if (searchType==S_QUERY_SEARCH){
            queryBuilder.must()
                    .add(QueryBuilders.matchQuery("detailText",searchVal)
                            .fuzziness(Fuzziness.AUTO));
        }
        else if (searchType== S_DATE_SEARCH){
            String dateStr = searchVal.toString();
            String startDateTimeStr = dateStr.toString()+":00";
            String eneDateTimeStr = dateStr.toString()+":59";
            queryBuilder.must()
                    .add(QueryBuilders.rangeQuery("detailCreateTime")
                            .gte(startDateTimeStr)
                            .lte(eneDateTimeStr));
        }

        return queryBuilder;
    }

}
