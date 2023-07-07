package publicInterface;

import com.markyao.model.dto.RestData;

public interface RpcHarvestService {
    RestData startHarvestUrl(String url,String title);
    RestData startHarvestUrlAndMonitor(String url,String title);

    RestData stopHarvestUrl();

    RestData startHarvestComments();

    RestData pauseHarvestComments();

    RestData stopHarvestComments();

    RestData curDealingCounts();
}
