package MicroRpc.framework.loadbalance.impl;

import MicroRpc.framework.beans.Invoker;
import MicroRpc.framework.loadbalance.AbstractLoadBalance;
import MicroRpc.framework.redis.Registry.core.RedisRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class RandomLoadBalance extends AbstractLoadBalance {
    private final ConcurrentHashMap<String,RandomSelector>randomSelectors=new ConcurrentHashMap<>(256);

    @Override
    public String doSelect(Invoker invoker) {
        String selectKey = invoker.getInterfaceName() + ":" + invoker.getMethodName();
        RandomSelector selector = randomSelectors.get(selectKey);
        if (selector!=null && selector.selectUrlMap.size()==1){
            return selector.select();
        }
        int providerCount = checkProviderCount(invoker.getInterfaceName());
        if (selector==null || providerCount!=selector.providerCount){
            selector=new RandomSelector(providerCount,invoker);
            randomSelectors.put(selectKey,selector);
        }
        return selector.select();
    }

    private static class RandomSelector{
        private final ConcurrentHashMap<Integer,String>selectUrlMap=new ConcurrentHashMap<>(16);
        private final ConcurrentHashMap<Integer,Integer>selectHelpMap=new ConcurrentHashMap<>(16);
        private final Map<String,Integer>urlToWeight;
        private final int providerCount;
        private final int total;
        private final Random random=new Random();
        public RandomSelector(int providerCount,Invoker invoker) {
            this.providerCount=providerCount;
            urlToWeight=new HashMap<>();
            int sum=0;
            int idx=0;
            Map<String, String> mp = RedisRegistry.getUrlForWeights(invoker.getInterfaceName());
            for (Map.Entry<String, String> e : mp.entrySet()) {
                String url = e.getKey();
                int weight=Integer.parseInt(e.getValue());
                selectUrlMap.put(idx++,url);
                urlToWeight.put(url,weight);
                sum+=weight;
            }
            total=sum;
            idx=0;
            for (Map.Entry<Integer, String> e : selectUrlMap.entrySet()) {
                int index = e.getKey();
                String url = e.getValue();
                int weight = urlToWeight.get(url);
                for (int i = idx; i < idx+weight; i++) {
                    selectHelpMap.put(i,index);
                }
                idx+=weight;
            }
        }

        public String select(){
            int idx = random.nextInt(total);
            int index = selectHelpMap.get(idx);
            return selectUrlMap.get(index);
        }
    }
}
