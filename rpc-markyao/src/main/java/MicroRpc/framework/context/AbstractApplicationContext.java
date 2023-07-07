package MicroRpc.framework.context;

import MicroRpc.framework.loadbalance.impl.ConsistentHashLoadBalance;
import MicroRpc.framework.loadbalance.impl.RandomLoadBalance;
import MicroRpc.framework.redis.Registry.core.RedisRegistry;
import MicroRpc.framework.loadbalance.impl.RoundRobinLoadBalance;
import MicroRpc.framework.loadbalance.LoadBalance;
import MicroRpc.framework.tools.StringUtils;
import MicroRpc.framework.protocol.Protocol;
import MicroRpc.framework.beans.ProtocolTypes;
import MicroRpc.framework.beans.Url;
import MicroRpc.framework.protocol.dubbo.springjson.DubboProtocol;
//import MicroRpc.framework.beans.MicroRpc.framework.protocol.http.HttpProtocol;
import MicroRpc.framework.tools.builders.AppnameBuilder;
import MicroRpc.framework.tools.IO.DubboRegistryXmlBuilder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractApplicationContext implements DubboFactory{
    protected final Set<String>registedServices=new HashSet<>();
    protected final DubboRegistryXmlBuilder registryXmlBuilder=new DubboRegistryXmlBuilder();
    protected final ClassLoader classLoader=Thread.currentThread().getContextClassLoader();
    protected final Protocol protocol;
    protected final LoadBalance selector;

    public AbstractApplicationContext(String protocolType,Integer loadbalanceType){
        LoadBalance selector=null;
        switch (loadbalanceType){
            case LoadBalance.ROUND_ROBIN_WEIGHT:
                selector=new RoundRobinLoadBalance();
                break;
            case LoadBalance.CONSISTENT_HASH:
                selector=new ConsistentHashLoadBalance();
                break;
            case LoadBalance.RANDAM_WEIGHT:
                selector=new RandomLoadBalance();
                break;
        }
        Protocol protocol=null;
        switch (protocolType){
            case ProtocolTypes.HTTP:
//                protocol=new HttpProtocol(selector);
                protocol=null;
                break;
            case ProtocolTypes.DUBBO:
                protocol=new DubboProtocol(selector);
                break;
            default:
                protocol=new DubboProtocol(selector);
                break;
        }

        this.protocol=protocol;
        this.selector=selector;
    }

    public AbstractApplicationContext(Protocol protocol, LoadBalance selector,ApplicationContext applicationContext) {
        this.protocol = protocol;
        this.selector = selector;
    }

    public void refresh() throws ClassNotFoundException {
        //读取服务的配置文件
        DubboRegistryXmlBuilder.DubboRegistryBean dubboRegistryBean = registryXmlBuilder.buildDubboRegistryBean();

        //初始化redis配置
        RedisRegistry.init(dubboRegistryBean.getRedisConfig());

        Url url=new Url(dubboRegistryBean.getHost(),Integer.parseInt(dubboRegistryBean.getPort()));
        Integer weight=null;
        if (null!=dubboRegistryBean.getWeight()) {
            weight=Integer.parseInt(dubboRegistryBean.getWeight());
        }
        Map<String,Class>servicesMp=null;
        if (dubboRegistryBean.getServiceProvider().size()>0) {
            servicesMp=new HashMap<>();
            for (Map.Entry<String, String> e : dubboRegistryBean.getServiceProvider().entrySet()) {
                String key = e.getKey();
                Class<?> clazz = classLoader.loadClass(e.getValue());
                servicesMp.put(key,clazz);
            }
        }
        Set<String> serviceRefrence = dubboRegistryBean.getServiceRefrence();
        doRegist(dubboRegistryBean.getAppName(),url,weight,servicesMp, serviceRefrence);

        if (null!=servicesMp && servicesMp.size()>0){
            //开启服务
            startService(url);
        }
    }

    public void doRegist(String appName, Url url,Integer weight, Map<String,Class>servicesMp,Set<String> refrences){
        if (!StringUtils.hasText(appName)) {
            final String defaultAppname = AppnameBuilder.getDefaultAppname(url);
            appName=defaultAppname;
        }
        registAppname(appName);

        registUrl(appName,url);

        if (null == weight) {
            weight = 1;
        }

        registWeight(appName,weight);

        if (null!=servicesMp && servicesMp.size()>0){
            for (Map.Entry<String, Class> e : servicesMp.entrySet()) {
                final String itfName = e.getKey();
                final Class implClazz = e.getValue();
                registServer(appName,itfName,implClazz);
            }
        }

        if (null!=refrences && refrences.size()>0){
            for (String refrence : refrences) {
                registRefrence(appName,refrence);
            }
        }

        registedServices.add(appName);
    }

    @Override
    public abstract void registAppname(String appName);

    @Override
    public abstract void registUrl(String appname, Url url);

    @Override
    public abstract void registWeight(String appname, Integer weight);

    @Override
    public abstract void registServer(String appname, String interfaceName, Class implClazz);

    @Override
    public abstract void registRefrence(String appname, String interfaceName);

    public void startService(Url url){
        protocol.recv(url);
    }

    public void startService(Url url, ConfigurableListableBeanFactory beanFactory){
        protocol.recv(url,beanFactory);
    }


    public Protocol getProtocol() {
        return protocol;
    }

    public void close(){
//        clearUpRegistry();
    }

    private void clearUpRegistry(){
        RedisRegistry.clearAll();
    }
}
