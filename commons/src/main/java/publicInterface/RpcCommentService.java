package publicInterface;

import com.markyao.model.dto.RestData;

public interface RpcCommentService {
    RestData listPage(Integer currentPage, Integer pageSize);

    /**
     * 旧接口,走mysql
     * @param awemeId
     * @param currentPage
     * @param pageSize
     * @param searchType
     * @param searchMsg
     * @param sortType
     * @return
     */
    RestData pages(String awemeId, Integer currentPage, Integer pageSize, Integer searchType, String searchMsg, String sortType);

    /**
     * 走es
     * @param awemeId
     * @param currentPage
     * @param pageSize
     * @param searchType
     * @param searchMsg
     * @param sortType
     * @param sortRet
     * @return
     */
    RestData pages(String awemeId, Integer currentPage, Integer pageSize, Integer searchType, String searchMsg, String sortType,int sortRet);



}
