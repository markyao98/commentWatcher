package MicroRpc.framework.loadbalance.impl;

import MicroRpc.framework.loadbalance.AbstractLoadBalance;
import MicroRpc.framework.loadbalance.LoadBalance;
import MicroRpc.framework.redis.Registry.core.RedisRegistry;
import MicroRpc.framework.beans.Invoker;
import MicroRpc.framework.beans.Url;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j(topic = "m.RoundRobinLoadBalance")
public class RoundRobinLoadBalance extends AbstractLoadBalance implements LoadBalance {
    private final Map<String,RoundRobinSelector>selectorMap=new ConcurrentHashMap<>();


    @Override
    public String doSelect(Invoker invoker) {
        int providerCount = checkProviderCount(invoker.getInterfaceName());
        String interfaceName = invoker.getInterfaceName();
        RoundRobinSelector selector = selectorMap.get(interfaceName);
        if (null==selector || providerCount!=selector.providerCount){
            selector=new RoundRobinSelector(interfaceName,providerCount);
            selectorMap.put(interfaceName,selector);
        }
        String url = selector.rrAddWeight();
        return url;
    }


    private static class RoundRobinSelector{
    private final LinkedHashMap<String,Integer> weights=new LinkedHashMap<>();
    private final LinkedHashMap<String,Integer>currents=new LinkedHashMap<>();;
    private final LinkedHashMap<String,Integer>effectives=new LinkedHashMap<>();
    private int totalWeights;
    private final int maxFails=3;//最大失败次数
    private final int ERROR_WEIGHT=-127;
    private final int providerCount;

    public RoundRobinSelector(String interfaceName,int providerCount) {
        this.providerCount=providerCount;
        int sum=0;
        Map<String, String> mp = RedisRegistry.getUrlForWeights(interfaceName);
        for (Map.Entry<String, String> e : mp.entrySet()) {
            String key = e.getKey();
            int wi=Integer.parseInt(e.getValue());
            weights.put(key,wi);
            currents.put(key,wi);
            effectives.put(key,wi);
            sum+=wi;
        }
        totalWeights=sum;
    }
    private String rrAddWeight() {
        if (currents.size()<=1){
            for (String url : currents.keySet()) {
                return url;
            }
        }
        checkFail();
        checkError();
        int max=0;
        String maxKey=null;
        for (Map.Entry<String, Integer> e : currents.entrySet()) {
            if (e.getValue()!=ERROR_WEIGHT && max<e.getValue()){
                max=e.getValue();
                maxKey=e.getKey();
            }else if (e.getValue()==ERROR_WEIGHT){
                log.error("检测到已经挂掉的连接:  {}",e.getKey());
            }
        }
//        //拿到最大key,去减total
        if (maxKey!=null){
            int newCurr = currents.get(maxKey) - totalWeights;
            currents.put(maxKey,newCurr);
        }
        log.info("选择连接: {} 目标权值为 {}",maxKey,max);
//        //遍历+eff
        for (String k : currents.keySet()) {
            if (ERROR_WEIGHT==currents.get(k))
                continue;
            currents.put(k,currents.get(k)+effectives.get(k));
        }
        return maxKey;


    }
    private void checkError() {
        List<String> urls=new ArrayList<>(16);
        for (Map.Entry<String, Integer> e : currents.entrySet()) {
            String url = e.getKey();
            if (RedisRegistry.isError(url)){
                urls.add(url);
            }
        }
        for (String url : urls) {
            currents.remove(url);
            effectives.remove(url);
            totalWeights-=weights.get(url);
            weights.remove(url);
        }
    }
    private void checkFail() {
        if (!RedisRegistry.containFails()){
            resetEffectives();
            return;
        }
        Map<String, String> failMap = RedisRegistry.getFailMap();
        for (Map.Entry<String, Integer> e : effectives.entrySet()) {
             String url = e.getKey();
             Integer wt = e.getValue();
            if (failMap.containsKey(url)){
                int newEff = (wt - weights.get(url)) / maxFails;
                effectives.put(url,newEff);
            }
        }
    }

    private void resetEffectives() {
        for (Map.Entry<String, Integer> e : effectives.entrySet()) {
            Integer wt = weights.get(e.getKey());
            if (e.getValue()!= wt){
                effectives.put(e.getKey(), wt);
            }
        }
    }

}


}
