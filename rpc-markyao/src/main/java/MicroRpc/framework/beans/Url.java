package MicroRpc.framework.beans;

import java.io.Serializable;

public class Url implements Serializable {

    private String host;
    private Integer port;

    public Url() {
    }

    public Url(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
