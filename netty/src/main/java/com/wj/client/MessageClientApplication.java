package com.wj.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/**
 * @author w
 * {@code @time:} 11:16
 * Description: netty客户端启动类
 */
public class MessageClientApplication {
    private String ip;
    private int port;

    public MessageClientApplication(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void run() throws InterruptedException {

        // IO线程池
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        // 客户端辅助启动类
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventExecutors)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(ip, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new MessageHandlerClient());
                        }
                    });
            // 建立远程连接 同步
            ChannelFuture future = bootstrap.connect().sync();
            // 发送消息
            future.channel().writeAndFlush(Unpooled.copiedBuffer("Hello World", CharsetUtil.UTF_8));
            // 等待关闭操作完成
            future.channel().closeFuture().sync();
        } finally {
            eventExecutors.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new MessageClientApplication("192.168.1.88",8888).run();
    }
}
