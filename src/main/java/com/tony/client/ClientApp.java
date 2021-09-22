/**
 * 
 */
package com.tony.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

/**
 * @author  tony
 * @version 1.0
 * @date    2021-09-17 16:41:42
 *
 */

public class ClientApp {

  public static void main(String[] args) {
    EventLoopGroup mainGroup = new NioEventLoopGroup();
    Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(mainGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
      @Override
      protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
          @Override
          public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(Unpooled.copiedBuffer("这是客户端发送的消息！", CharsetUtil.UTF_8));
          }

          @Override
          public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf bb = (ByteBuf) msg;
            System.out.println("收到服务端 " + ctx.channel().remoteAddress() + " 的消息：" + bb.toString(CharsetUtil.UTF_8));
          }
        });
      }
    });
    System.out.println("客户端已启动。。。");
    try {
      ChannelFuture future = bootstrap.connect("127.0.0.1", 8888).sync();
      future.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      mainGroup.shutdownGracefully();
    }
  }

}
