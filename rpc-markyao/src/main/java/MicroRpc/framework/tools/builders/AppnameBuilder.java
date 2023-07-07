package MicroRpc.framework.tools.builders;

import MicroRpc.framework.beans.Url;

public class AppnameBuilder {

    public static String getDefaultAppname(Url url){
        return url.getHost()+":"+url.getPort()+"-app";
    }
}
