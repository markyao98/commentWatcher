package MicroRpc.framework.context;

import MicroRpc.framework.beans.ProtocolTypes;
import MicroRpc.framework.loadbalance.LoadBalance;
import MicroRpc.framework.redis.Registry.LocalRegistry;
import MicroRpc.framework.redis.Registry.core.RedisRegistry;
import MicroRpc.framework.excption.DubboException;
import MicroRpc.framework.beans.Url;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.context.ApplicationContext;

/**
 * * defalut for redis Registry.
 */
public class DefaultDubboApplicationContext extends AbstractApplicationContext{

    public DefaultDubboApplicationContext(ApplicationContext context) {
        super(ProtocolTypes.DUBBO, LoadBalance.RANDAM_WEIGHT);

    }

    public DefaultDubboApplicationContext(String protocolType, Integer loadbalanceType) {
        super(protocolType, loadbalanceType);
    }


    @Override
    public void registAppname(String appName) {
        RedisRegistry.registAppName(appName);
    }

    @Override
    public void registUrl(String appname, Url url) {
        RedisRegistry.registUrl(appname,url);
    }

    @Override
    public void registWeight(String appname, Integer weight) {
        RedisRegistry.registWeight(appname,weight);
    }

    @Override
    public void registServer(String appname, String interfaceName, Class implClazz) {
        try {
            RedisRegistry.registServer0(appname,interfaceName,implClazz);
            LocalRegistry.regist(interfaceName,implClazz);
        } catch (JsonProcessingException e) {
            throw new DubboException("注册服务失败");
        }
    }

    @Override
    public void registRefrence(String appname, String interfaceName) {
        RedisRegistry.registRefrence(appname,interfaceName);
    }


}
