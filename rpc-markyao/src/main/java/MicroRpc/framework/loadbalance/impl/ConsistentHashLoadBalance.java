package MicroRpc.framework.loadbalance.impl;
import MicroRpc.framework.loadbalance.AbstractLoadBalance;
import MicroRpc.framework.loadbalance.LoadBalance;
import lombok.extern.slf4j.Slf4j;
import MicroRpc.framework.excption.DubboException;
import MicroRpc.framework.redis.Registry.core.RedisRegistry;
import MicroRpc.framework.beans.Invoker;
import MicroRpc.framework.beans.Url;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
//一致性哈希负载均衡策略
@Slf4j(topic = "m.ConsistentHashLoadBalance")
public class ConsistentHashLoadBalance extends AbstractLoadBalance implements LoadBalance {
    private final Map<String, ConsistentHashSelector> selectorMap=new ConcurrentHashMap<>();


    @Override
    public String doSelect(Invoker invoker) {
        String[] urls= RedisRegistry.getProviderUrlStrByIntefaceName(invoker.getInterfaceName());
        //检查url列表是否变动过以及当前selector是否不存在.
        int providerCount = checkProviderCount(invoker.getInterfaceName());
        if (providerCount==0){
            return null;
        }
        String key=invoker.getInterfaceName()+"."+invoker.getMethodName();
        ConsistentHashSelector selector = selectorMap.get(key);

        if (null == selector || providerCount!=selector.providerCount){
            log.info("重建treeMap");
            selector = new ConsistentHashSelector(providerCount,urls,invoker);
            selectorMap.put(key,selector);
        }
        String url = selector.select(invoker);
        return url;
    }


    private static class ConsistentHashSelector{
        private final int providerCount;
        private final TreeMap<Long,String>virtualInvokers;
        private final int replicaNumber;

        private final static int DEFAULT_REPLICANUMBER=160;
        //构造一致性散列表。
        public ConsistentHashSelector(int providerCount, String[] urls, Invoker invoker) {
            this.providerCount=providerCount;
            this.virtualInvokers=new TreeMap<>();
            this.replicaNumber=DEFAULT_REPLICANUMBER;

            String interfaceName = invoker.getInterfaceName();
            String methodName = invoker.getMethodName();
            for (String url : urls) {
                String key=url+":"+interfaceName+"."+methodName;
                for (int i = 0; i < replicaNumber / 4; i++) {
                    //md5计算出16个字节的数组
                    byte[] digest = md5(key + i);
                    for (int h = 0; h < 4; h++) {
                        //四次散列,分别是0-3位，4-7位....
                        long m = hash(digest, h);
                        //放进treeMap
                        virtualInvokers.put(m,url);
                    }
                }
            }
         }

    private long hash(byte[] digest, int number) {
        return (((long) (digest[3 + number * 4] & 0xFF) << 24)
                | ((long) (digest[2 + number * 4] & 0xFF) << 16)
                | ((long) (digest[1 + number * 4] & 0xFF) << 8)
                | (digest[number * 4] & 0xFF))
                & 0xFFFFFFFFL;
    }

    private byte[] md5(String value) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        md5.reset();
        byte[] bytes = null;
        try {
            bytes = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        md5.update(bytes);
        return md5.digest();
    }

        public String select(Invoker invoker) {
            String key = tokey(invoker);
            byte[] digest = md5(key);
            Map.Entry<Long, String> e = virtualInvokers.tailMap(hash(digest, 0), true).firstEntry();

            if (null==e){
                e=virtualInvokers.firstEntry();
            }
            log.info("选择连接:  {}",e.getValue());
            return e.getValue();
        }

        private String tokey(Invoker invoker) {
            StringBuilder sb=new StringBuilder();
            sb.append(invoker.getInterfaceName()).append(invoker.getMethodName());
            for (Object param : invoker.getParams()) {
                sb.append(param.toString());
            }
            return sb.toString();
        }
    }
}







