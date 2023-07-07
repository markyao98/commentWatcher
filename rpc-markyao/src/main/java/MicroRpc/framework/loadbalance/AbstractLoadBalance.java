package MicroRpc.framework.loadbalance;

import MicroRpc.framework.beans.Invoker;
import MicroRpc.framework.beans.Url;
import MicroRpc.framework.redis.Registry.core.RedisRegistry;
import lombok.extern.slf4j.Slf4j;

import static MicroRpc.framework.redis.Registry.core.RedisRegistry.getUrlForStr;

@Slf4j(topic = "m.AbstractLoadBalance")
public abstract class AbstractLoadBalance implements LoadBalance{

    protected Url retry(int rev, Invoker invoker) {
        if (rev>=3){
            return null;
        }
        String urls = doSelect(invoker);
        if (null != urls){
            return getUrlForStr(urls);
        }
        log.error("负载均衡重试第 {} 次...",rev);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return retry(rev+1,invoker);
    }


    private String getAppName(String urls){
        return RedisRegistry.getAppname(urls);
    }

    public int checkProviderCount(String interfaceName){
        return RedisRegistry.getProviderCount(interfaceName);
    }

    public boolean checkHeartBeat(String url){
        String appName=getAppName(url);
        return checkHeartBeat(appName,url);
    }

    public boolean checkHeartBeat(String appName,String url){
        String heartBeatKey = RedisRegistry.buildHeartBeatKey(appName, url);
        return RedisRegistry.hasKey(heartBeatKey);
    }

    @Override
    public Url selectUrl(Invoker invoker){
        String url = doSelect(invoker);
        if (url==null || url.length()<=0){
            boolean tiktok = checkHeartBeat(url);
            if (tiktok)
                return getUrlForStr(url);
            else {
                log.error("目标url无心跳,稍等重试..");
                return retry(0,invoker);
            }
        }
        return getUrlForStr(url);
    }

    public abstract String doSelect(Invoker invoker);
}
