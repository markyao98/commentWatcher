package MicroRpc.framework.protocol.dubbo.springjson;

import MicroRpc.framework.beans.Invoker;
import MicroRpc.framework.beans.Url;
import MicroRpc.framework.excption.CommException;
import MicroRpc.framework.loadbalance.LoadBalance;
import MicroRpc.framework.protocol.handler.AbstractProtocolHandler;
import MicroRpc.framework.redis.Registry.core.RedisRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//json版本
@Slf4j(topic = "m.DubboProtocol")
public class DubboProtocol extends AbstractProtocolHandler {
    private final int MAX_CLINENT_CAPACITY=4;
    private final List<DubboClient>dubboClientPools=new ArrayList<>(MAX_CLINENT_CAPACITY);
    {
        for (int i = 0; i < MAX_CLINENT_CAPACITY; i++) {
            dubboClientPools.add(new DubboClient());
        }
    }
    private final Object clientLock=new Object();
    private volatile int clientRet=0;
    private final DubboClient dubboClient=new DubboClient();
    private final ObjectMapper objectMapper=new ObjectMapper();
    public final static String ERROR_MSG="目标url已断开连接,为你重新分配合适的服务:";
    public DubboProtocol(LoadBalance loadBalance) {
        super(loadBalance);
//        log.info("建立连接中...");
//        for (int i = 0; i < MAX_CLINENT_CAPACITY; i++) {
//            send(new Invoker(BindInvoker.interfaceName,BindInvoker.methodName,null,null),1);
//        }
    }

    @Override
    public Object send(Invoker invoker,int waitSec) {
        Url url = loadBalance.selectUrl(invoker);
        if (url==null){
            log.error("当前服务提供者列表没有对应的服务.");
            return null;
        }
        Object result=null;
        try {
            int retry=0;
            synchronized (clientLock){
                clientRet=(clientRet+1)%MAX_CLINENT_CAPACITY;
            }
            DubboClient dubboClient=dubboClientPools.get(clientRet);
            while (retry<3){
                result = dubboClient.send(url, invoker,waitSec);
                if (result==null){
                    log.info("【可无视】返回空,重试中... {}",retry);
                    retry++;
                }else {
                    break;
                }
            }
            return result;
        } catch (CommException | InterruptedException | NullPointerException  e) {
            RedisRegistry.adviceFail(url);
            log.error("invoker发送时异常 {} ,目标url: {}",e.getMessage(),url.getHost()+":"+url.getPort());
        }
        return null;
    }


    @Override
    public void recv(Url url, ConfigurableListableBeanFactory beanFactory) {
        NettyServer.serverStart0(url,beanFactory);

    }

    @Override
    public void recv(Url url) {
        NettyServer.serverStart0(url,null);
    }

}
