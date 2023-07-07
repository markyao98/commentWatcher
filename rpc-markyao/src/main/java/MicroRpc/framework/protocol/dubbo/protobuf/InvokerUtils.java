package MicroRpc.framework.protocol.dubbo.protobuf;

import MicroRpc.framework.beans.Invoker;
import MicroRpc.framework.tools.serializable.BytesUtils;
import com.google.protobuf.ByteString;

public class InvokerUtils {

    public static Invoker invokerProto2Invoker(InvokerProto.InvokerMsg invokerMsg){
        String interfaceName = invokerMsg.getInterfaceName();
        String methodName = invokerMsg.getMethodName();
        ByteString paramsTypeBYS = invokerMsg.getParamsType();
        ByteString paramsBYS = invokerMsg.getParams();

        Class[] pamramstypes = (Class[]) BytesUtils.ByteToObject(BytesUtils.bystringToBys(paramsTypeBYS));
        Object[] params = (Object[]) BytesUtils.ByteToObject(BytesUtils.bystringToBys(paramsBYS));
//
//        Class[] pamramstypes = (Class[]) BytesUtils.ByteToObject(BytesUtils.bystringToBys(paramsTypeBYS));
//        Object[] params = (Object[]) BytesUtils.ByteToObject(BytesUtils.bystringToBys(paramsBYS));

        Invoker invoker=new Invoker();
        invoker.setInterfaceName(interfaceName);
        invoker.setMethodName(methodName);
        invoker.setParams(params);
        invoker.setParamsType(pamramstypes);
        return invoker;
    }


    public static InvokerProto.InvokerMsg invoker2InvokerProto(Invoker invoker){
        ByteString parmasTypeBystring = BytesUtils.bysToBystring(BytesUtils.ObjectToByte(invoker.getParamsType()));
        ByteString parmasBystring = BytesUtils.bysToBystring(BytesUtils.ObjectToByte(invoker.getParams()));

        InvokerProto.InvokerMsg iv = InvokerProto.InvokerMsg.newBuilder()
                .setMethodName(invoker.getMethodName())
                .setInterfaceName(invoker.getInterfaceName())
                .setParamsType(parmasTypeBystring)
                .setParams(parmasBystring)
                .build();
        return iv;
    }
}
