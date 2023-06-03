package com.markyao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.markyao.mapper.CommentDetailsMapper;
import com.markyao.mapper.CommentUserMapper;
import com.markyao.mapper.MonitorCommentDiggMapper;
import com.markyao.model.pojo.CommentDetails;
import com.markyao.model.pojo.MonitorCommentDigg;
import com.markyao.service.CommentDetailService;
import com.markyao.service.harvest.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CommentDetailServiceImpl extends ServiceImpl<CommentDetailsMapper, CommentDetails> implements CommentDetailService {
    @Autowired
    MonitorCommentDiggMapper monitorCommentDiggMapper;
    @Autowired
    CommentDetailsMapper commentDetailsMapper;

    /**
     *  通过aid 更新评论点赞量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long updateStatusForAid(String aid) {
        List<CommentDetails> commentDetails = this.getBaseMapper().selectList(new QueryWrapper<CommentDetails>().eq("aweme_id", aid));
        //查询监控表
        long sum=0;
        for (CommentDetails commentDetail : commentDetails) {
            List<MonitorCommentDigg> monitorCommentDiggList = monitorCommentDiggMapper.selectList
                    (new QueryWrapper<MonitorCommentDigg>().eq("cdid", commentDetail.getId()).orderByDesc("create_time"));
            if (monitorCommentDiggList!=null && monitorCommentDiggList.size()>0){
                MonitorCommentDigg monitorCommentDigg = monitorCommentDiggList.get(0);
                if (!commentDetail.getDiggCount().equals(monitorCommentDigg.getDigCount()) || !commentDetail.getReplyCommentTotal().equals(monitorCommentDigg.getReplyCommentTotal())){
                    Long cdid = commentDetail.getId();
                    if (needTosave(cdid,commentDetail)){
                        //存放创世块
                        MonitorCommentDigg firstMonitor=new MonitorCommentDigg();
                        firstMonitor.setCreateTime(commentDetail.getCreateTime()).setCdid(cdid).
                                setIsAuthorDigged(commentDetail.getIsAuthorDigged()).setDigCount(commentDetail.getDiggCount())
                                .setReplyCommentTotal(commentDetail.getReplyCommentTotal()).setId(0L);
                        monitorCommentDiggMapper.insert(firstMonitor);
                        log.info("插入创世块~");
                    }


                    commentDetail.setDiggCount(monitorCommentDigg.getDigCount());
                    commentDetail.setReplyCommentTotal(monitorCommentDigg.getReplyCommentTotal());
                    commentDetailsMapper.updateById(commentDetail);
                    sum++;
                    log.info("更新点赞量与评论量~~");
                }
            }
        }
        return sum;
    }

    private boolean needTosave(Long cdid, CommentDetails commentDetail) {
        Date least = monitorCommentDiggMapper.selectMinCreateTimeByCdid(cdid);
        if (commentDetail.getCreateTime().before(least)){
            //需要把最早的点赞数据保存为监控数据
            return true;
        }
        return false;
    }


}
