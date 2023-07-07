package MicroRpc.framework.protocol.dubbo.springjson;

import MicroRpc.framework.beans.Invoker;
import MicroRpc.framework.beans.Url;
import MicroRpc.framework.excption.DubboException;
import MicroRpc.framework.protocol.dubbo.protobuf.InvokerProto;
import MicroRpc.framework.protocol.dubbo.protobuf.InvokerUtils;
import MicroRpc.framework.protocol.dubbo.springjson.initializer.BysProtocol;
import MicroRpc.framework.redis.Registry.core.RedisRegistry;
import MicroRpc.framework.tools.serializable.BytesUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.SerializationUtils;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j(topic = "m.DubboClientHandler")
public class DubboClientHandler extends SimpleChannelInboundHandler<BysProtocol> {
    private Invoker invoker;
    private Object result;
    private ChannelHandlerContext context;
    public void setInvoker(Invoker invoker) {
        long start = System.currentTimeMillis();
        this.invoker=invoker;
        result=null;
        BysProtocol bysProtocol=new BysProtocol();
        byte[] bytes = BytesUtils.ObjectToByte(invoker);
        bysProtocol.setContent(bytes);
        bysProtocol.setLen(bytes.length);
        context.writeAndFlush(bysProtocol);
        long end = System.currentTimeMillis();
        if (end-start>3){
            log.info("invokerset 耗时: {}",(end-start));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context=ctx;
    }

    @Override
    protected synchronized void channelRead0(ChannelHandlerContext channelHandlerContext, BysProtocol msg) throws Exception {
        byte[] bytes = msg.getContent();
        //todo 直接将bytes序列化成Object?
        Object object = BytesUtils.ByteToObject(bytes);
        this.result=object;
//        String r=new String(bytes,StandardCharsets.UTF_8);
//        this.result=r;
        notifyAll();
    }

    public synchronized Object getResult(int waitSec) {
        if (null==result){
            try {
                wait(waitSec*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    @Override
    public synchronized void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        shutdownCurrService(socketAddress);
        ctx.close();
        notifyAll();
        throw new DubboException("与主机的连接异常==>"+ socketAddress);
    }

    @Override
    public synchronized void channelInactive(ChannelHandlerContext ctx) throws Exception {
        result="close#channel";
        notifyAll();
    }

    private void shutdownCurrService(SocketAddress socketAddress){
        String s = socketAddress.toString();
        String urls = s.split("\\/")[1];
        String host = urls.split(":")[0];
        String port = urls.split(":")[1];
        Url url=new Url(host,Integer.parseInt(port));
        RedisRegistry.adviceError(url,invoker.getInterfaceName());
    }


}
