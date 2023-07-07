package publicInterface.test;

import com.markyao.model.vo.CommentDetailsVo;

import java.util.List;

public interface TestService {

    CommentDetailsVo getCommentDetailsVo();

    List<CommentDetailsVo>getCommentDetailsVoList();
}
