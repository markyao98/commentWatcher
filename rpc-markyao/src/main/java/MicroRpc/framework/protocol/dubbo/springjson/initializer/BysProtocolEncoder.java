package MicroRpc.framework.protocol.dubbo.springjson.initializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

//编码器
//编码器--> 将自定义的协议类型转化为字节数组
public class BysProtocolEncoder extends MessageToByteEncoder<BysProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, BysProtocol msg, ByteBuf out) throws Exception {
        int len = msg.getLen();
        byte[] content = msg.getContent();
        out.writeInt(len);
        out.writeBytes(content);
    }
}