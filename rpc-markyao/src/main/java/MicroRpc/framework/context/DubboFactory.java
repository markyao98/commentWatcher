package MicroRpc.framework.context;

import MicroRpc.framework.beans.Url;

public interface DubboFactory {

    void registAppname(String appName);

    void registUrl(String appname, Url url);

    void registWeight(String appname,Integer weight);

    void registServer(String appname,String interfaceName,Class implClazz);

    void registRefrence(String appname,String interfaceName);


}
