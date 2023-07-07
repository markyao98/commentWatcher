package com.markyao.es;

import com.markyao.common.States;
import com.markyao.model.dto.RestData;
import com.markyao.model.vo.CommentVo;
import com.markyao.model.vo.PageVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

@SpringBootTest
public class T2 {
    @Autowired
    CommentEsService commentEsService;

    @Test
    void t1(){
        RestData pages = commentEsService.pages("7199620500274941239", 1, 3, States.S_QUERY_SEARCH, "日本", States.ORDER_COMMON
                , 1);
        PageVo pageVo = (PageVo) pages.getData();
        List<CommentVo> list = (List<CommentVo>) pageVo.getList();
        for (CommentVo commentVo : list) {
            System.out.println(commentVo);
        }
    }
}
