package MicroRpc.framework.context;

import MicroRpc.framework.beans.ProtocolTypes;
import MicroRpc.framework.beans.Url;
import MicroRpc.framework.commons.ServiceProvider;
import MicroRpc.framework.commons.ServiceRefrence;
import MicroRpc.framework.excption.DubboException;
import MicroRpc.framework.loadbalance.LoadBalance;
import MicroRpc.framework.redis.Registry.LocalRegistry;
import MicroRpc.framework.redis.Registry.core.RedisRegistry;
import MicroRpc.framework.tools.IO.AnnotationUtils;
import MicroRpc.framework.tools.IO.DubboRegistryXmlBuilder;
import MicroRpc.framework.tools.IO.IOUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * * Rpc for Spring.
 */
@Slf4j
public class SpringDubboApplicationContext extends AbstractApplicationContext{
    private final ConfigurableListableBeanFactory springBeanFactory;
    private final Map<Class,Integer>refrencesWaittimeMp=new ConcurrentHashMap<>(16);
     public SpringDubboApplicationContext(ConfigurableListableBeanFactory beanFactory, String protocolType, Integer loadbalanceType) {
        super(protocolType, loadbalanceType);
        this.springBeanFactory=beanFactory;
    }



    public <T> T getService(Class<T> clazz,int waitSec){
        return this.getProtocol().getService(clazz,waitSec);
    }
    public <T> T getService(String interfaceName,int waitSec){
        return this.getProtocol().getService(interfaceName,waitSec);
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

    @Override
    public void refresh()  {
        try {
            this.refresh0();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        log.info("Rpc for Spring启动");
    }

    private void refresh0() throws ClassNotFoundException {
        /**
         * 读取注解配置方式的》。。
         */
        //读取服务的配置文件
        DubboRegistryXmlBuilder.DubboRegistryBean dubboRegistryBean = registryXmlBuilder.buildDubboRegistryBean();

        //初始化redis配置
        RedisRegistry.init(dubboRegistryBean.getRedisConfig());

        Url url=new Url(dubboRegistryBean.getHost(),Integer.parseInt(dubboRegistryBean.getPort()));
        Integer weight=null;
        if (null!=dubboRegistryBean.getWeight()) {
            weight=Integer.parseInt(dubboRegistryBean.getWeight());
        }

        //读取服务生产者/消费者
        Map<String,Class> servicesMp=new HashMap<>();
        if (dubboRegistryBean.getServiceProvider().size()>0) {
            servicesMp=new HashMap<>();
            for (Map.Entry<String, String> e : dubboRegistryBean.getServiceProvider().entrySet()) {
                String key = e.getKey();
                Class<?> clazz = classLoader.loadClass(e.getValue());
                servicesMp.put(key,clazz);
            }
        }

        Set<String> serviceRefrence = dubboRegistryBean.getServiceRefrence();
        if (serviceRefrence==null){
            serviceRefrence=new HashSet<>();
        }
        //注解扫描,写进Map
        registServiceForAnnotation(servicesMp,serviceRefrence);
        doRegist(dubboRegistryBean.getAppName(),url,weight,servicesMp, serviceRefrence);
        System.out.println("【Rpc服务】  "+new Date()+ " 注册服务: "+servicesMp.size()+" 个");
        System.out.println("【Rpc服务】  "+new Date()+ " 引用服务: "+serviceRefrence.size()+" 个");
        if (null!=servicesMp && servicesMp.size()>0){
            new Thread(()->{
                startService(url,this.springBeanFactory);
            },"SPRING-PRC-STARTER").start();
        }
        sleep(3000);
        //实例化消费者
        initSpringBeansForRefrences();
        //处理定制化等待的时间
        initRefrencesWaitTimes();
    }

    /**
     * 初始化消费者调用等待时间
     */
    private void initRefrencesWaitTimes() {
        for (Map.Entry<Class, Integer> e : refrencesWaittimeMp.entrySet()) {
            Class clazz = e.getKey();
            Integer waitSec = e.getValue();

        }
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 通过注解注册服务生产者/消费者
     * @param servicesMp
     * @param serviceRefrence
     */
    private void registServiceForAnnotation(Map<String, Class> servicesMp, Set<String>serviceRefrence) {
        List<Class> clazzs = IOUtils.scanClassForTotalFilePath();
        for (Class clazz : clazzs) {
            if (AnnotationUtils.isAnnotationOfXX(clazz, ServiceProvider.class)){
                //todo 注册服务生产者
                for (Class anInterface : clazz.getInterfaces()) {
                    String name = anInterface.getName();
                    servicesMp.put(name,clazz);
                }
            }
            List<Class> fieldClazz = AnnotationUtils.getFieldAnnotationByXx(clazz, ServiceRefrence.class);
            if (fieldClazz !=null){
                //todo 注册服务消费者
                for (Class aClass : fieldClazz) {
                    String name = aClass.getName();
                    serviceRefrence.add(name);
                }
            }
        }
    }


    /**
     * 初始化spring容器内带有refrenceService注解的bean，为他们成员变量赋值
     */
    private void initSpringBeansForRefrences() {
        String[] beanDefinitionNames = this.springBeanFactory.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = this.springBeanFactory.getBean(beanDefinitionName);
            Class<?> clazz = bean.getClass();
            List<Field> fieldList = AnnotationUtils.getFieldAnnotationByXx0(clazz, ServiceRefrence.class);
            if (fieldList!=null && !fieldList.isEmpty()){
                for (Field field : fieldList) {
                    field.setAccessible(true);
                    try {
                        int waitSec = field.getAnnotation(ServiceRefrence.class).waitSec();
                        refrencesWaittimeMp.put(field.getType(),waitSec);
                        field.set(bean,this.getService(field.getType(),waitSec));
                        log.info("注入Service成功!! {} 【waitSec: {}秒 】",field.getType(),waitSec);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
