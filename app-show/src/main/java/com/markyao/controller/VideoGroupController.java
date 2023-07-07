package com.markyao.controller;

import com.markyao.model.dto.RestData;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import publicInterface.RpcVideoGroupService;

/**
 * 视频分组前端控制器
 */

@RestController
public class VideoGroupController {


    @DubboReference
    RpcVideoGroupService rpcVideoGroupService;

    @GetMapping("group/byG")
    public RestData videosByGNotIN(){
        return rpcVideoGroupService.videosByGNotIN();

    }
    @GetMapping("group/byG/{id}")
    public RestData videosByG(@PathVariable("id")String id){
        return rpcVideoGroupService.videosByG(id);

    }
    @GetMapping("groupinfo/{id}")
    public RestData groupinfo(@PathVariable("id")String id){
        return rpcVideoGroupService.groupinfo(id);

    }
    //addgroup
    @PostMapping("/addgroup")
    public RestData handleFormSubmit(@RequestParam("desc") String desc,
                                     @RequestParam("title") String title,
                                     @RequestParam("img") MultipartFile img) {
        return rpcVideoGroupService.handleFormSubmit(desc,title,img);

    }

    @GetMapping("/group/addVideo")
    public RestData groupaddVideo(@RequestParam("vids") String[] vids,@RequestParam("gid")String gid){
        return rpcVideoGroupService.groupaddVideo(vids,gid);

    }
}
