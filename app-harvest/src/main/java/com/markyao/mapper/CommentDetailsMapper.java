package com.markyao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.markyao.model.pojo.CommentDetails;
import com.markyao.model.pojo.IpModel;

import java.util.List;


public interface CommentDetailsMapper extends BaseMapper<CommentDetails> {

    int insertForId(CommentDetails commentDetails);

    List<IpModel> selectIpModelDesc(String awemeId);

    List<IpModel> selectIpModelDescByAids(String[] aids);

    List<Long>searchByWord(String awemeId,String query);

    List<Long>searchByIpLabel(String awemeId,String ipLabel);

    List<Long>searchByWordSort(String awemeId,String query,String sortField,Integer current,Integer pageSize);

    List<Long>searchByIpLabelSort(String awemeId,String ipLabel,String sortField,Integer current,Integer pageSize);
}
