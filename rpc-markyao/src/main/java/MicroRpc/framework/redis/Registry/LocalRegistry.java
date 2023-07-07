package MicroRpc.framework.redis.Registry;

import java.util.HashMap;

public class LocalRegistry {

    private static final HashMap<String,Class>serviceMap=new HashMap<>();


    public static void regist(String interfaceName,Class clazz){
        serviceMap.put(interfaceName,clazz);

    }
    public static Class getImpl(String interfaceName){
        return serviceMap.get(interfaceName);

    }


}
