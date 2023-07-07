package MicroRpc.framework.protocol.dubbo.springjson;

import MicroRpc.framework.beans.Url;
import MicroRpc.framework.protocol.dubbo.springjson.initializer.BysProtocolDecoder;
import MicroRpc.framework.tools.StringUtils;
import MicroRpc.framework.excption.DubboException;
import MicroRpc.framework.protocol.dubbo.protobuf.InvokerProto;
import MicroRpc.framework.protocol.dubbo.springjson.initializer.BysProtocolEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;

@Slf4j(topic = "m.NettyServer")
public class NettyServer {
    public static void serverStart0(Url url, ConfigurableListableBeanFactory beanFactory){
        if (!StringUtils.hasText(url.getHost()) || null==url.getPort())
            throw new DubboException("检查Url是否书写正确");

        serverStart1(url,beanFactory);
    }


    private static void serverStart1(Url url, ConfigurableListableBeanFactory beanFactory){
        EventLoopGroup bossGroup=new NioEventLoopGroup(1);
        // 核数*总等待时间 / cpu等待时间 约等于 8 *100 /10 =80  ==>Runtime.getRuntime().availableProcessors() * 10
        EventLoopGroup wokerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap=new ServerBootstrap();
        serverBootstrap
                .group(bossGroup,wokerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
//                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(new BysProtocolDecoder());
//                        pipeline.addLast(new ProtobufDecoder(InvokerProto.InvokerMsg.getDefaultInstance()));
                        pipeline.addLast(new BysProtocolEncoder());
                        pipeline.addLast(new ServerHandler(beanFactory));
                    }
                });
        ChannelFuture channelFuture = serverBootstrap.bind(url.getHost(), url.getPort());
        log.info("dubbo-server 开始提供服务.");
        try {
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            wokerGroup.shutdownGracefully();
        }
    }

}
