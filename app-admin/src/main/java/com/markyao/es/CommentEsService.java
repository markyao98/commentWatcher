package com.markyao.es;

import com.markyao.model.dto.RestData;

public interface CommentEsService {
    RestData pages(String awemeId, int current, int pageSize, int searchType, Object searchMsg, String sortType,int sortRet) ;

}
