package com.markyao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.markyao.model.pojo.CommentUser;


public interface CommentUserMapper extends BaseMapper<CommentUser> {

    int insertForId(CommentUser commentUser);
}
