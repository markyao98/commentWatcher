package com.markyao.controller;

import com.markyao.model.dto.RestData;
import com.markyao.service.harvest.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("comments")
public class CommentController {

    @Autowired
    CommentService commentService;


    @GetMapping("list")
    public RestData listPage(@RequestParam(value = "page",defaultValue = "1") Integer currentPage,@RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        return commentService.pages(currentPage, pageSize);
    }

    @GetMapping("list/{awemeId}")
    public RestData listPage(@RequestParam(value = "page",defaultValue = "1") Integer currentPage,
                             @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                             @PathVariable("awemeId")String awemeId,
                             @RequestParam(value = "searchType",defaultValue = "0")Integer searchType,
                             @RequestParam(value = "searchMsg")String searchMsg,
                             @RequestParam(value = "sortType",defaultValue = "0")String sortType
                             ) throws ParseException {
        return commentService.pages(awemeId,currentPage, pageSize,searchType,searchMsg,sortType);
    }
}
