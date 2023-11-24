package com.wj.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author w
 * {@code @time:} 11:46
 * Description: netty server boot class
 */
@Slf4j
public class MessageServerApplication {
    private int port;

    public MessageServerApplication(int port) {
        this.port = port;
    }

    public void run() throws InterruptedException {
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(eventExecutors)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new MessageHandlerServer());
                        }
                    });

            // 绑定服务器
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            log.info("在" + channelFuture.channel().localAddress() + "上开启监听");

            channelFuture.channel().closeFuture().sync();
        } finally {
            eventExecutors.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {

        MessageServerApplication messageServerApplication = new MessageServerApplication(8888);
        messageServerApplication.run();
    }
}
