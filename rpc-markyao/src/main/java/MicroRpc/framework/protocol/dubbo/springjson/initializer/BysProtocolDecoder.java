package MicroRpc.framework.protocol.dubbo.springjson.initializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

//解码器
//解码器 ->将字节数组解码成协议类型
public class BysProtocolDecoder extends ReplayingDecoder<Void> {//ReplayingDecoder<Void> 可以更方便的解码
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int len = in.readInt();//读取长度
        byte[] bytes = new byte[len];
        in.readBytes(bytes);//读取字节数组
        BysProtocol protocol = new BysProtocol();
        protocol.setLen(len);
        protocol.setContent(bytes);
        out.add(protocol);
    }
}