package com.markyao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.markyao.model.pojo.CdidMinLeast;
import com.markyao.model.pojo.MonitorCommentDigg;

import java.util.Date;
import java.util.List;

public interface MonitorCommentDiggMapper  extends BaseMapper<MonitorCommentDigg> {

    List<Long>selectAids();

    List<CdidMinLeast>selectMinCreateTimeForCd();

    Date selectMinCreateTimeByCdid(Long cdid);
}
