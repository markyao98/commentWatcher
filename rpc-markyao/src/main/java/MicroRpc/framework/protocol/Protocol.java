package MicroRpc.framework.protocol;

import MicroRpc.framework.beans.Invoker;
import MicroRpc.framework.beans.Url;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;

public interface Protocol {

     Object send(Invoker invoker,int waitSec);


     void recv(Url url, ConfigurableListableBeanFactory beanFactory);


     void recv(Url url);

     <T> T getService(String interfaceName,int waitSec);

     <T> T getService(Class clazz,int waitSec);
}
