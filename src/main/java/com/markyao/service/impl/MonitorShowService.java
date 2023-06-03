package com.markyao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.markyao.mapper.CommentDetailsMapper;
import com.markyao.mapper.MonitorCommentDiggMapper;
import com.markyao.model.pojo.CommentDetails;
import com.markyao.model.pojo.MonitorCommentDigg;
import com.markyao.model.vo.MonitorShowVo;
import com.markyao.utils.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.markyao.utils.DateFormatUtils.getFormat;

@Service
public class MonitorShowService {
    @Autowired
    MonitorCommentDiggMapper monitorCommentDiggMapper;
    @Autowired
    CommentDetailsMapper commentDetailsMapper;
    public MonitorShowVo showForDid(String did) {
//        CommentDetails createDeatils = commentDetailsMapper.selectById(did);//创世块 但是它的点赞量有可能是有问题的
        List<MonitorCommentDigg> monitorCommentDiggList =
                monitorCommentDiggMapper.selectList(new QueryWrapper<MonitorCommentDigg>().eq("cdid", did).orderByAsc("create_time"));

        List<Object>timeData=new ArrayList<>(monitorCommentDiggList.size()+1);
        List<Object>likeData=new ArrayList<>(monitorCommentDiggList.size()+1);
        List<Object>replyData=new ArrayList<>(monitorCommentDiggList.size()+1);

//        Integer firstDig = createDeatils.getDiggCount();
//        Integer firstReply = createDeatils.getReplyCommentTotal();
//        String firstDateTime = getFormat(createDeatils.getCreateTime());
//        timeData.add(firstDateTime);
//        likeData.add(firstDig);
//        replyData.add(firstReply);

        for (MonitorCommentDigg monitorCommentDigg : monitorCommentDiggList) {
            likeData.add(monitorCommentDigg.getDigCount());
            replyData.add(monitorCommentDigg.getReplyCommentTotal());
            timeData.add(getFormat(monitorCommentDigg.getCreateTime()));
        }

        MonitorShowVo showVo=new MonitorShowVo();
        showVo.setReplyData(replyData).setLikeData(likeData).setTimeData(timeData);

        return showVo;
    }
}
