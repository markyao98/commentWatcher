package com.markyao.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.markyao.mapper.CommentDetailsMapper;
import com.markyao.mapper.CommentMapper;
import com.markyao.mapper.CommentUserMapper;
import com.markyao.model.pojo.Comment;
import com.markyao.model.pojo.CommentDetails;
import com.markyao.model.pojo.CommentUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
public class DeduplicationTest {

    @Autowired
    CommentMapper commentMapper;
    @Autowired
    CommentDetailsMapper commentDetailsMapper;
    @Autowired
    CommentUserMapper commentUserMapper;

    /**
     * @Description 脏数据处理器
     * @Author markyao
     * @Date  2023/5/24
     */
    @Test
    void t1(){
//        String aid="7230753691257605409";
//        String aid="7234826414799752481";
        String aid="7234101592927112460";
        AtomicInteger total= new AtomicInteger();

        List<Comment> commentList = commentMapper.selectList(new QueryWrapper<Comment>().eq("aid", aid));
        commentList.stream().forEach(
                c->{
                    CommentUser commentUser = commentUserMapper.selectById(c.getUid());
                    if (commentUser==null){
                        commentMapper.deleteById(c.getId());
                        commentDetailsMapper.deleteById(c.getDid());
                        total.getAndIncrement();
                        log.info("处理脏数据+1");
                    }

                }
        );
        log.info("总共处理了{} 条脏数据",total.get());
    }

    @Autowired
    DuplicateService duplicateService;
    /**
     * 通过评论的cid删除脏数据
     */
    @Test
    void t2(){
        String cids[]={"7239736454199935783","7239736411749172025","7239736288369558312","7239735962937934596",
                "7239737957535515451","7239735299617260323","7239740407034495800",
                "7239740066574304032","7239740188372845327","7239743075081339659","7239734700494750467"};
        duplicateService.deleteAllByCids(cids);

    }

}
