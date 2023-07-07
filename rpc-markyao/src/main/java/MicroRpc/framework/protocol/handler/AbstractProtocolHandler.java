package MicroRpc.framework.protocol.handler;

import MicroRpc.framework.beans.Invoker;
import MicroRpc.framework.loadbalance.LoadBalance;
import MicroRpc.framework.protocol.Protocol;
import MicroRpc.framework.beans.Url;
import MicroRpc.framework.tools.beanutils.MapToObjectConverter;
import com.fasterxml.jackson.databind.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Proxy;
import java.util.Map;

@Slf4j(topic = "m.AbstractProtocolHandler")
public abstract class AbstractProtocolHandler implements Protocol {
    protected final LoadBalance loadBalance;
    public AbstractProtocolHandler(LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
    }

    @Override
    public abstract Object send(Invoker invoker,int waitSec);



    protected <T>T getProxy(Class clazz,int waitSec) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{clazz},
                (proxy, method, args) -> {
                    Class[] argsTypes = buildTypes(args);
                    Invoker invoker = new Invoker(clazz.getName(), method.getName(), argsTypes, args);
                    Object result = send(invoker,waitSec);
                    // TODO: 2023/6/14 解决由map无法转化为object的bug
                    if (result instanceof Map){
                        Class<?> returnType = method.getReturnType();
                        result= MapToObjectConverter.convert((Map<String, Object>) result,returnType);
                    }
                    return result;
                }
        );
    }

    private Class[] buildTypes(Object[] args) {
        if (null == args || args.length==0)
            return null;
        Class[] cs=new Class[args.length];
        int i=0;
        for (Object a : args) {
            cs[i++]=a.getClass();
        }
        return cs;
    }

    @Override
    public <T> T getService(String interfaceName,int waitSec){
        Class clazz = null;
        try {
            clazz = ClassLoader.getSystemClassLoader().loadClass(interfaceName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return getService(clazz,waitSec);
    }

    @Override
    public <T> T getService(Class clazz,int waitSec){
        T proxy = getProxy(clazz,waitSec);
        return proxy;
    }
}
