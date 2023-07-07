package publicInterface.test;

import com.markyao.model.dto.RestData;

public interface TestRpcIndexService {
    RestData getVideos();
    RestData indexMonitorsData();

    RestData indexGroups();
}
