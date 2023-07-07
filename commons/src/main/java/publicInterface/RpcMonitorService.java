package publicInterface;

import com.markyao.model.dto.RestData;

public interface RpcMonitorService {
    RestData digCount(String[] aids, Integer timeStand);

    RestData monitorStop(String[] aids);

    RestData monitorIsGoing(String aid);

   

    RestData monitorIsGoingList();

    RestData monitorList();

    RestData monitorBoomList();

    RestData monitorShow(String did);
}
