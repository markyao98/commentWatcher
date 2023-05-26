package com.markyao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.markyao.model.pojo.Comment;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface CommentMapper extends BaseMapper<Comment> {


    @Select("select * from harvest_comment c left join harvest_comment_details cd on cd.id=c.did" +
            "where c.aid=#{awemeId} order by cd.${sortField}")
    List<Comment> selectPageOrder(String awemeId, String sortField);
}
