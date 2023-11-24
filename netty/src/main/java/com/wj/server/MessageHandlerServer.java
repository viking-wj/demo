package com.wj.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author w
 * {@code @time:} 11:39
 * Description: server message handler
 */
@Slf4j
public class MessageHandlerServer extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        log.info("server 接收消息：" + in.toString(CharsetUtil.UTF_8));

        ctx.writeAndFlush(Unpooled.copiedBuffer("server has already receive message!",CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       log.error("异常",cause);
       ctx.close();
    }
}
