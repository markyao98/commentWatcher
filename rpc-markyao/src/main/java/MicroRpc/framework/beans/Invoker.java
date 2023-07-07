package MicroRpc.framework.beans;

import java.io.Serializable;

public class Invoker implements Serializable {

    private String interfaceName;
    private String methodName;
    private Class[] paramsType;
    private Object[] params;

    public Invoker() {

    }

    public Invoker(String interfaceName, String methodName, Class[] paramsType, Object[] params) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.paramsType = paramsType;
        this.params = params;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class[] getParamsType() {
        return paramsType;
    }

    public void setParamsType(Class[] paramsType) {
        this.paramsType = paramsType;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
