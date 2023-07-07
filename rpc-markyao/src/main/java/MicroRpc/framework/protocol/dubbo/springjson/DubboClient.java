package MicroRpc.framework.protocol.dubbo.springjson;

import MicroRpc.framework.beans.Invoker;
import MicroRpc.framework.beans.Url;
import MicroRpc.framework.protocol.dubbo.springjson.initializer.BysProtocolEncoder;
import MicroRpc.framework.tools.StringUtils;
import MicroRpc.framework.excption.CommException;
import MicroRpc.framework.protocol.dubbo.springjson.initializer.BysProtocolDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j(topic = "m.DubboClient")
public class DubboClient {

    private final Map<String, DubboClientHandler>urlHadnlers=new ConcurrentHashMap<>(16);
    private final Object syncLock=new Object();
    private Thread dobind=null;


    private void dobind(Url url, DubboClientHandler cli) throws InterruptedException {
        EventLoopGroup group;
        Bootstrap bootstrap;
        if (StringUtils.hasText(url.getHost()) && url.getPort()!=null){
            group=new NioEventLoopGroup();
            bootstrap=new Bootstrap();
            bootstrap
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new BysProtocolEncoder());
//                            pipeline.addLast(new ProtobufEncoder()); //proto编码器
                            pipeline.addLast(new BysProtocolDecoder());
                            pipeline.addLast(cli);
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(url.getHost(), url.getPort());//sync的作用是阻塞,意思是让连接阻塞
            log.info("连接成功 {}",url);
        }
    }

    public Object send(Url url, Invoker invoker,int waitSec) throws InterruptedException {
        String urlkey = getUrlkey(url);
        if (!urlHadnlers.containsKey(urlkey)){
            DubboClientHandler clientHandler = new DubboClientHandler();
            urlHadnlers.put(getUrlkey(url),new DubboClientHandler());
            synchronized (urlkey.intern()){
                new Thread(() -> {
                    try {
                        dobind(url,clientHandler);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                },"bind-thread-"+urlkey).start();
            }
            int retry=0;
            while (retry<10){
                try {
                    clientHandler.setInvoker(invoker);
                    Object result = clientHandler.getResult(waitSec);
                    if ("close#channel".equals(result.toString())){
                        log.info("连接已关闭,将会尝试重连..");
                        urlHadnlers.remove(urlkey);
                        return null;
                    }
                    if (null==result){
                        throw new CommException("目标返回空");
                    }
                    urlHadnlers.put(urlkey,clientHandler);
                    return result;
                } catch (NullPointerException e) {
                    Thread.sleep(1000);
                    log.info("第一次与 {} 建立连接,请稍候.",urlkey);
                }finally {
                    retry++;
                }
            }
        }

        synchronized (syncLock){
            Object result = null;
            DubboClientHandler dubboClientHandler = urlHadnlers.get(urlkey);
            try {
                dubboClientHandler.setInvoker(invoker);
            } catch (NullPointerException e) {
                log.info("context为null,将会尝试重连..{}",urlkey);
                urlHadnlers.remove(urlkey);
                return null;
            }
            result = dubboClientHandler.getResult(waitSec);
            if (result!=null && "close#channel".equals(result.toString())){
                log.info("连接已关闭,将会尝试重连..{}",urlkey);
                urlHadnlers.remove(urlkey);
            }
            return result;
        }
    }

    private String getUrlkey(Url url){
        return url.getHost()+":"+url.getPort();
    }


}
