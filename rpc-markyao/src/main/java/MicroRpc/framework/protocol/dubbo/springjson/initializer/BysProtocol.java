package MicroRpc.framework.protocol.dubbo.springjson.initializer;

import lombok.Data;

/**
 * 解决TCP粘包，拆包问题——协议类对象
 */
@Data
public class BysProtocol {
    //包含长度+字节数组
    private int len;
    private byte[] content;
}
