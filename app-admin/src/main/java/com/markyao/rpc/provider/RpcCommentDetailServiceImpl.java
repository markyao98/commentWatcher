package com.markyao.rpc.provider;

import MicroRpc.framework.commons.ServiceProvider;
import com.markyao.model.dto.RestData;
import com.markyao.service.CommentDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import publicInterface.RpcCommentDetailService;

@Component
@ServiceProvider
public class RpcCommentDetailServiceImpl implements RpcCommentDetailService {
    @Autowired
    CommentDetailService commentDetailService;

    @Override
    public RestData updateStatus(String awemeId) {
        long sum = commentDetailService.updateStatusForAid(awemeId);
        if (sum==0){
            return RestData.success(0).setMsg("当前视频下未新增监控数据");
        }
        return RestData.success(sum).setMsg("更新"+sum+" 条监控数据成功");
    }
}
