package publicInterface;

import com.markyao.model.dto.RestData;
import org.springframework.web.multipart.MultipartFile;

public interface RpcVideoGroupService {
    RestData videosByGNotIN();

    RestData videosByG(String id);

    RestData groupinfo(String id);

    RestData handleFormSubmit(String desc, String title, MultipartFile img);

    RestData groupaddVideo(String[] vids, String gid);
}
