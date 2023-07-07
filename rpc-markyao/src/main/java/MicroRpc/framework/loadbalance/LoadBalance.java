package MicroRpc.framework.loadbalance;

import MicroRpc.framework.beans.Invoker;
import MicroRpc.framework.beans.Url;

public interface LoadBalance {
    int ROUND_ROBIN_WEIGHT=1; //轮询方式
    int CONSISTENT_HASH=2;//一致性哈希式
    int RANDAM_WEIGHT=3;//随机方式

    Url selectUrl(Invoker invoker);


}
