package com.markyao.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.markyao.dao.CommentDetailsDao;
import com.markyao.dao.CommentUserDao;
import com.markyao.dao.HarvestIndexWalDao;
import com.markyao.dao.VideoDao;
import com.markyao.model.dto.EsCommentDto;
import com.markyao.model.pojo.*;
import com.markyao.service.EsStorageService;
import com.markyao.utils.ConfigUtils;
import com.markyao.utils.SnowflakeIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.util.Date;

/**
 * es存储
 */
@Slf4j
public class EsStorageServiceImpl implements EsStorageService {
    private final CommentDetailsDao commentDetailsDao=new CommentDetailsDao();
    private final CommentUserDao commentUserDao=new CommentUserDao();
    private final VideoDao videoDao=new VideoDao();
    private final HarvestIndexWalDao walDao=new HarvestIndexWalDao();
    private final ObjectMapper objectMapper=new ObjectMapper();
    private final SnowflakeIdGenerator idGenerator=new SnowflakeIdGenerator(1,1);

/*    private final static RestHighLevelClient client;
    static {
        client= new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
    }*/

    private final static String INDEX_NAME= ConfigUtils.getValue("es.index");
    private final static String TYPE_NAME=ConfigUtils.getValue("es.type");

    private final static String esHost=ConfigUtils.getValue("es.host");
    private final static int esPort= Integer.parseInt(ConfigUtils.getValue("es.port"));


    @Override
    public void save(Comment comment) {
        // TODO: 2023/6/12 WAL日志创建
        HarvestIndexWal harvestIndexWallog=new HarvestIndexWal();
        long generateId = idGenerator.generateId();
        harvestIndexWallog.setCid(comment.getId()).setCreateTime(new Date()).setUpdateTime(new Date())
                .setLogType(1).setEventStatus(0).setId(generateId).setRetryCnt(0);
        walDao.save(harvestIndexWallog);
        log.info("WAL日志插入~");

        EsCommentDto esCommentDto=new EsCommentDto();
        //查询details表和user表
        CommentDetails details = commentDetailsDao.selectDetailsById(comment.getDid());
        CommentUser user = commentUserDao.selectUserById(comment.getUid());
        //查询video表
        VideoInfo videoInfo = videoDao.selectVideoByaId(comment.getAid());
        esCommentDto.setId(comment.getId())
                .setDetailAwemeId(details.getAwemeId())
                .setDetailCid(details.getCid()).setDetailId(details.getId()).setDetailCur(details.getCur()).setDetailCount(details.getCount())
                .setDetailCreateTime(details.getCreateTime()).setDetailIpLabel(details.getIpLabel()).setDetailText(details.getText()).setDetailIsAuthorDigged(details.getIsAuthorDigged())
                .setDetailDiggCount(details.getDiggCount()).setDetailReplyCommentTotal(details.getReplyCommentTotal())
                .setUserAvatar(user.getAvatar()).setUserId(user.getId()).setUserUserAge(user.getUserAge()).setUserLanguage(user.getLanguage())
                .setUserNickname(user.getNickname()).setUserRegion(user.getRegion()).setUserSecUid(user.getSecUid())
                .setVideoTitle(videoInfo.getTitleInfo());
        //持久化至es
        try(RestHighLevelClient client=new RestHighLevelClient(RestClient.builder(new HttpHost(esHost, esPort, "http")))) {
            String jsonString = objectMapper.writeValueAsString(esCommentDto);
            // 创建 IndexRequest
            IndexRequest request = new IndexRequest(INDEX_NAME, TYPE_NAME)
                    .source(jsonString, XContentType.JSON);
            try {
                // 执行插入操作
                IndexResponse response = client.index(request, RequestOptions.DEFAULT);
                //TODO WAL日志成功状态更新
                walDao.updateStatus(harvestIndexWallog,1);
                log.info("WAL日志更新,记录成功");
            } catch (IOException e) {
                //TODO WAL日志失败状态更新
                walDao.updateStatus(harvestIndexWallog,2);
                log.info("WAL日志更新,记录失败");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void close() {
        try (RestHighLevelClient client= new RestHighLevelClient(RestClient.builder(new HttpHost(esHost, esPort, "http")))){
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
