package com.markyao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.markyao.model.pojo.VideoGroup;
import com.markyao.model.pojo.VideoInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface VideoGroupMapper extends BaseMapper<VideoGroup> {

    @Select("SELECT * FROM video_info WHERE id IN(SELECT info_id FROM video_info_group WHERE group_id=#{gid})")
    List<VideoInfo> getVideosByGid(String gid);
    @Select("SELECT * FROM video_info where id IN(SELECT id FROM video_info WHERE id NOT IN(SELECT info_id FROM video_info_group)) ")
    List<VideoInfo> getVideosByNotIn();

    void addVideosForGroup(@Param("vids") String[] vids,@Param("gid")String gid);

    @Select("SELECT * FROM video_group WHERE id=(SELECT group_id FROM video_info_group WHERE aid=#{aid})")
    VideoGroup selectByAid(String aid);

    @Select("SELECT * FROM video_group WHERE id=(SELECT group_id FROM video_info_group WHERE info_id=#{vid})")
    VideoGroup selectByVid(Long vid);
}
