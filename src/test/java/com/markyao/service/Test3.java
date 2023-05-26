package com.markyao.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.markyao.mapper.CommentDetailsMapper;
import com.markyao.mapper.CommentMapper;
import com.markyao.mapper.CommentUserMapper;
import com.markyao.model.pojo.Comment;
import com.markyao.model.pojo.CommentDetails;
import com.markyao.model.pojo.CommentUser;
import com.markyao.model.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@SpringBootTest
public class Test3 {
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    CommentDetailsMapper commentDetailsMapper;
    @Autowired
    CommentUserMapper commentUserMapper;

    @Test
    void t1(){
        String aid1="7230753691257605409";
        String aid2="7234826414799752481";
        String aid3="7234101592927112460";

        List<Comment> c1 = commentMapper.selectList(new QueryWrapper<Comment>().eq("aid", aid1));
        List<Long> uids1 = c1.stream().map(c -> c.getUid()).collect(Collectors.toList());
        List<CommentUser> users1 = commentUserMapper.selectList(new QueryWrapper<CommentUser>().in("id", uids1));


        List<Comment> c2 = commentMapper.selectList(new QueryWrapper<Comment>().eq("aid", aid2));
        List<Long> uids2 = c2.stream().map(c -> c.getUid()).collect(Collectors.toList());
        List<CommentUser> users2= commentUserMapper.selectList(new QueryWrapper<CommentUser>().in("id", uids2));

        List<Comment> c3 = commentMapper.selectList(new QueryWrapper<Comment>().eq("aid", aid3));
        List<Long> uids3 = c3.stream().map(c -> c.getUid()).collect(Collectors.toList());
        List<CommentUser> users3 = commentUserMapper.selectList(new QueryWrapper<CommentUser>().in("id", uids3));

        Map<String,List<UserVo>>mp=new HashMap<>();

        log.info("开始分析,总数:{} ,总计算次数: {}*{}*{}={} ",c1.size()+c2.size()+c3.size(),c1.size(),c2.size(),c3.size(),(c1.size()*c2.size()*c3.size()));
        log.info("====================================================");
        long total=0;
        long l = System.currentTimeMillis();
        for (CommentUser u1 : users1) {
            String sid1 = u1.getSecUid();
            if (sid1==null){
                continue;
            }
            sid1=sid1.trim();
            List<UserVo> l1 = mp.getOrDefault(sid1, new ArrayList<UserVo>());
//            CommentDetails d1 = getCommentDetails(u1.getId());
            UserVo userVo1 = new UserVo();
            userVo1.setId(u1.getId()).setSecUid(u1.getSecUid());
            l1.add(userVo1);
            mp.put(sid1,l1);

            for (CommentUser u2 : users2) {
                String sid2 = u2.getSecUid();
                if (sid2==null){
                    continue;
                }
                sid2=sid2.trim();
                List<UserVo> l2 = mp.getOrDefault(sid2, new ArrayList<>());
//                CommentDetails d2 = getCommentDetails(u2.getId());
                UserVo userVo2 = new UserVo();
                userVo2.setId(u2.getId()).setSecUid(u2.getSecUid());
                l2.add(userVo2);
                mp.put(sid2,l2);
                if (l2.size()>1){
                    log.info("发现相同的了.. {}",sid2);
                }
//                for (CommentUser u3 : users3) {
//                    String sid3 = u3.getSecUid();
//                    if (sid3==null){
//                        continue;
//                    }
//                    sid3=sid3.trim();
//                    List<UserVo> l3 = mp.getOrDefault(sid3, new ArrayList<>());
//                    CommentDetails d3 = getCommentDetails(u3.getId());
//                    UserVo userVo3 = new UserVo();
//                    userVo3.setId(u3.getId()).setSecUid(u3.getSecUid()).setDid(d3.getId()).setText(d3.getText());
//                    l3.add(userVo3);
//                    mp.put(sid3,l3);
//                }
            }
        }

        for (Map.Entry<String, List<UserVo>> entry : mp.entrySet()) {
            List<UserVo> list = entry.getValue();
            if (list.size()>1) {
                log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                log.info("secId: {} 发现相同用户: {}",entry.getKey(),list.size());

//                list.stream().forEach(u-> System.out.println(u));
                log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            }
        }
        long end = System.currentTimeMillis();
        log.info("分析结束 发现相同用户{}  耗时: {} s",total,(end-l));
    }

    @Test
    void t2(){
        String aid1="7230753691257605409";
        String aid2="7234826414799752481";
        String aid3="7234101592927112460";

        List<Comment> c1 = commentMapper.selectList(new QueryWrapper<Comment>().eq("aid", aid1));
        List<Long> uids1 = c1.stream().map(c -> c.getUid()).collect(Collectors.toList());
        List<CommentUser> users1 = commentUserMapper.selectList(new QueryWrapper<CommentUser>().in("id", uids1));


        List<Comment> c2 = commentMapper.selectList(new QueryWrapper<Comment>().eq("aid", aid2));
        List<Long> uids2 = c2.stream().map(c -> c.getUid()).collect(Collectors.toList());
        List<CommentUser> users2= commentUserMapper.selectList(new QueryWrapper<CommentUser>().in("id", uids2));

        List<Comment> c3 = commentMapper.selectList(new QueryWrapper<Comment>().eq("aid", aid3));
        List<Long> uids3 = c3.stream().map(c -> c.getUid()).collect(Collectors.toList());
        List<CommentUser> users3 = commentUserMapper.selectList(new QueryWrapper<CommentUser>().in("id", uids3));

        Map<String, Set<String>>mp=new HashMap<>();

        log.info("开始分析,总数:{} ,总计算次数: {}*{}*{}={} ",c1.size()+c2.size()+c3.size(),c1.size(),c2.size(),c3.size(),(c1.size()*c2.size()*c3.size()));
        log.info("====================================================");
        long total=0;
        long l = System.currentTimeMillis();
        for (CommentUser u1 : users1) {
            String sid1 = u1.getSecUid();
            if (sid1==null){
                continue;
            }
            sid1=sid1.trim();
            Set<String> l1 = mp.getOrDefault(sid1, new HashSet<>());
            UserVo userVo1 = new UserVo();
            userVo1.setId(u1.getId()).setSecUid(u1.getSecUid());
            l1.add(u1.getId()+"");
            mp.put(sid1,l1);

            for (CommentUser u2 : users2) {
                String sid2 = u2.getSecUid();
                if (sid2==null){
                    continue;
                }
                sid2=sid2.trim();
                if (mp.containsKey(sid2)){
                    Set<String> l2 = mp.get(sid2);
                    l2.add(u2.getId()+"");
                    mp.put(sid2,l2);
                }
                if (users3!=null){
                    for (CommentUser u3 : users3) {
                        String sid3 = u3.getSecUid();
                        if (sid3==null){
                            continue;
                        }
                        sid3 = sid3.trim();
                        if (mp.containsKey(sid3)){
                            Set<String> l3 = mp.get(sid3);
                            l3.add(u3.getId()+"");
                            mp.put(sid3,l3);
                        }
                    }
                }

            }

            if (mp.get(sid1).size()<=1){
                mp.remove(sid1);
            }
        }

        for (Map.Entry<String, Set<String>> entry : mp.entrySet()) {
            Set<String> list = entry.getValue();
            if (list.size()>1) {
                log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                log.info("secId: {} 发现相同用户: {}",entry.getKey(),list.size());
                list.stream().forEach(uid->{
                    Comment comment = commentMapper.selectOne(new QueryWrapper<Comment>().eq("uid", uid).select("did"));
                    CommentDetails commentDetails = commentDetailsMapper.selectOne(new QueryWrapper<CommentDetails>().eq("id", comment.getDid()).select("text"));
                    log.info("评论内容: {}",commentDetails.getText());
                });
//                list.stream().forEach(u-> System.out.println(u));
                log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            }
        }
        long end = System.currentTimeMillis();
        log.info("分析结束 发现相同用户{}  耗时: {} s",total,(end-l));
    }

    @Test
    void t3(){
        String aid1="7230753691257605409";
        String aid2="7234826414799752481";
        String aid3="7234101592927112460";
        List<Comment> c1 = commentMapper.selectList(new QueryWrapper<Comment>().eq("aid", aid1));
        List<Long> uids1 = c1.stream().map(c -> c.getUid()).collect(Collectors.toList());
        List<CommentUser> users1 = commentUserMapper.selectList(new QueryWrapper<CommentUser>().in("id", uids1));


        List<Comment> c2 = commentMapper.selectList(new QueryWrapper<Comment>().eq("aid", aid2));
        List<Long> uids2 = c2.stream().map(c -> c.getUid()).collect(Collectors.toList());
        List<CommentUser> users2= commentUserMapper.selectList(new QueryWrapper<CommentUser>().in("id", uids2));

        List<Comment> c3 = commentMapper.selectList(new QueryWrapper<Comment>().eq("aid", aid3));
        List<Long> uids3 = c3.stream().map(c -> c.getUid()).collect(Collectors.toList());
        List<CommentUser> users3 = commentUserMapper.selectList(new QueryWrapper<CommentUser>().in("id", uids3));

        users1.addAll(users2);
        users1.addAll(users3);
        Map<String, Set<String>>mp=new HashMap<>();

        for (CommentUser u1 : users1) {
            String sid1 = u1.getSecUid();
            if (sid1==null){
                continue;
            }
            sid1=sid1.trim();
            Set<String> l1 = mp.getOrDefault(sid1, new HashSet<>());
            UserVo userVo1 = new UserVo();
            userVo1.setId(u1.getId()).setSecUid(u1.getSecUid());
            l1.add(u1.getId()+"");
            mp.put(sid1,l1);
        }

        for (Map.Entry<String, Set<String>> entry : mp.entrySet()) {
            Set<String> list = entry.getValue();
            if (list.size()>1) {
                log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                log.info("secId: {} 发现相同用户: {}",entry.getKey(),list.size());
                list=deduplicationSet(list);
                list.stream().forEach(uid->{
                    Comment comment = commentMapper.selectOne(new QueryWrapper<Comment>().eq("uid", uid).select("did"));
                    CommentDetails commentDetails = commentDetailsMapper.selectOne(new QueryWrapper<CommentDetails>().eq("id", comment.getDid()).select("text"));
                    log.info("评论内容: {}",commentDetails.getText());
                });
//                list.stream().forEach(u-> System.out.println(u));
                log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            }
        }
    }

    private Set<String> deduplicationSet(Set<String> list) {
        Set<String>set=new HashSet<>();
        Set<String>result=new HashSet<>();
        for (String uid : list) {
            Comment comment = commentMapper.selectOne(new QueryWrapper<Comment>().eq("uid", uid).select("did"));
            CommentDetails commentDetails = commentDetailsMapper.selectOne(new QueryWrapper<CommentDetails>().eq("id", comment.getDid()).select("text","aweme_id"));
            String text = commentDetails.getText();
            String aid = commentDetails.getAwemeId();
            String key=text.trim()+aid;
            if (set.contains(key)){
                //todo 删除数据库多余的数据
                commentUserMapper.deleteById(uid);
                commentDetailsMapper.deleteById(commentDetails.getId());
                commentMapper.deleteById(comment.getId());
                log.info("去重成功!");
            }else {
                result.add(uid);
                set.add(key);
            }
        }
        return result;
    }

    private CommentDetails getCommentDetails(Long id) {
        Long did = commentMapper.selectOne(new QueryWrapper<Comment>().eq("uid", id)).getDid();
        return commentDetailsMapper.selectById(did);
    }
}
