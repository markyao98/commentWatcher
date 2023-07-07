package MicroRpc.framework.tools.IO;

import MicroRpc.framework.redis.Registry.utils.RedisRegistryConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DubboRegistryXmlBuilder {
    public final static String TAG_APPLICATION="application";
    public final static String TAG_APPLICATION_NAME="name";
    public final static String TAG_APPLICATION_HOST="host";
    public final static String TAG_APPLICATION_PORT="port";
    public final static String TAG_APPLICATION_WEIGHT="weight";

    public final static String TAG_SERVICEPROVIDER="serviceProvider";
    public final static String TAG_SERVICEPROVIDER_INTERFACENAME="interfaceName";
    public final static String TAG_SERVICEPROVIDER_IMPLCLASS="implClass";

    public final static String TAG_REFRENCE="serviceRefrence";
    public final static String TAG_REFRENCE_INTERFACENAME="interfaceName";

    public final static String TAG_REDIS="redis";
    public final static String TAG_REDIS_HOST="host";
    public final static String TAG_REDIS_PORT="port";
    public final static String TAG_REDIS_DB="db";
    public final static String TAG_REDIS_PASSWORD="password";
    public final static String TAG_REDIS_MAXTOTAL="maxtotal";


    public static void main(String[] args) throws ClassNotFoundException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Class<?> aClass = contextClassLoader.loadClass("MicroRpc.provider.HelloServiceImpl");
        System.out.println(aClass);
    }
    public DubboRegistryBean buildDubboRegistryBean(){
        DubboRegistryBean dubboRegistryBean=new DubboRegistryBean();
        try {
            Document document = XmlReader.initRead();

            RedisRegistryConfig redisRegistryConfig=buildRedisConfig(document);
            dubboRegistryBean.setRedisConfig(redisRegistryConfig);

            NodeList nList = document.getElementsByTagName(DubboRegistryXmlBuilder.TAG_APPLICATION);

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String appName = eElement.getElementsByTagName(TAG_APPLICATION_NAME).item(0).getTextContent();
                    String host = eElement.getElementsByTagName(TAG_APPLICATION_HOST).item(0).getTextContent();
                    String port = eElement.getElementsByTagName(TAG_APPLICATION_PORT).item(0).getTextContent();
                    NodeList wtnode = eElement.getElementsByTagName(TAG_APPLICATION_WEIGHT);
                    String weight=null;
                    if (null!=wtnode && wtnode.getLength()!=0)
                        weight = wtnode.item(0).getTextContent();
                    dubboRegistryBean
                            .setAppName(appName)
                            .setHost(host)
                            .setPort(port)
                            .setWeight(weight);
                }
            }

            NodeList serviceProvider = document.getElementsByTagName(TAG_SERVICEPROVIDER);
            for (int temp = 0; temp < serviceProvider.getLength(); temp++) {
                Node nNode = serviceProvider.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String interfaceName = eElement.getElementsByTagName(TAG_SERVICEPROVIDER_INTERFACENAME).item(0).getTextContent();
                    String implClass = eElement.getElementsByTagName(TAG_SERVICEPROVIDER_IMPLCLASS).item(0).getTextContent();
                    dubboRegistryBean.setServiceProvider(interfaceName,implClass);
                }
            }

            NodeList serviceRefrence = document.getElementsByTagName(TAG_REFRENCE);
            for (int temp = 0; temp < serviceRefrence.getLength(); temp++) {
                Node nNode = serviceRefrence.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String interfaceName = eElement.getElementsByTagName(TAG_REFRENCE_INTERFACENAME).item(0).getTextContent();
                    dubboRegistryBean.setServiceRefrence(interfaceName);
                }
            }


            return dubboRegistryBean;
        } catch (ParserConfigurationException  | IOException |SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    private RedisRegistryConfig buildRedisConfig(Document document) {
        RedisRegistryConfig defaultConfig=new RedisRegistryConfig("127.0.0.1",9527);
        defaultConfig.setDb(0);
        NodeList redisList = document.getElementsByTagName(TAG_REDIS);
        if (null==redisList || redisList.getLength()==0)
            return defaultConfig;

        for (int temp = 0; temp < redisList.getLength(); temp++) {
            Node nNode = redisList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                NodeList e1 = eElement.getElementsByTagName(TAG_REDIS_HOST);
                if (null!=e1 && e1.getLength()!=0)
                    defaultConfig.setHost(e1.item(0).getTextContent());

                NodeList e2 = eElement.getElementsByTagName(TAG_REDIS_PORT);
                if (null!=e2 && e2.getLength()!=0)
                    defaultConfig.setPort(Integer.parseInt(e2.item(0).getTextContent()));

                NodeList e3 = eElement.getElementsByTagName(TAG_REDIS_PASSWORD);
                if (null!=e3 && e3.getLength()!=0)
                    defaultConfig.setPassword(e3.item(0).getTextContent());

                NodeList e4 = eElement.getElementsByTagName(TAG_REDIS_MAXTOTAL);
                if (null!=e4 && e4.getLength()!=0)
                    defaultConfig.setMaxtotal(Integer.valueOf(e4.item(0).getTextContent()));

                NodeList e5 = eElement.getElementsByTagName(TAG_REDIS_DB);
                if (null!=e5 && e5.getLength()!=0)
                    defaultConfig.setDb(Integer.valueOf(e5.item(0).getTextContent()));
            }
        }
        return defaultConfig;
    }

    public class DubboRegistryBean{
        private String appName;
        private String host;
        private String port;
        private String weight;
        private Map<String,String> serviceProvider;
        private Set<String>serviceRefrence;
        private RedisRegistryConfig redisConfig;

        public DubboRegistryBean() {
            serviceProvider=new HashMap<>(16);
            serviceRefrence=new HashSet<>(16);
        }

        public RedisRegistryConfig getRedisConfig() {
            return redisConfig;
        }

        public void setRedisConfig(RedisRegistryConfig redisConfig) {
            this.redisConfig = redisConfig;
        }

        public DubboRegistryBean setServiceProvider(String itname, String implclass){
            serviceProvider.put(itname,implclass);
            return this;
        }

        public DubboRegistryBean setServiceRefrence(String refrence){
            serviceRefrence.add(refrence);
            return this;
        }

        public String getAppName() {
            return appName;
        }

        public DubboRegistryBean setAppName(String appName) {
            this.appName = appName;
            return this;
        }

        public String getHost() {
            return host;
        }

        public DubboRegistryBean setHost(String host) {
            this.host = host;
            return this;
        }

        public String getPort() {
            return port;
        }

        public DubboRegistryBean setPort(String port) {
            this.port = port;
            return this;
        }

        public Map<String, String> getServiceProvider() {
            return serviceProvider;
        }

        public DubboRegistryBean setServiceProvider(Map<String, String> serviceProvider) {
            this.serviceProvider = serviceProvider;
            return this;

        }

        public Set<String> getServiceRefrence() {
            return serviceRefrence;
        }

        public DubboRegistryBean setServiceRefrence(Set<String> serviceRefrence) {
            this.serviceRefrence = serviceRefrence;
            return this;
        }

        public String getWeight() {
            return weight;
        }

        public DubboRegistryBean setWeight(String weight) {
            this.weight = weight;
            return this;
        }
    }

}
