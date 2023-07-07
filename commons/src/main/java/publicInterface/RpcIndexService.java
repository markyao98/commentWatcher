package publicInterface;

import com.markyao.model.dto.RestData;

public interface RpcIndexService {
    RestData getVideos();
    RestData indexMonitorsData();

    RestData indexGroups();
}
