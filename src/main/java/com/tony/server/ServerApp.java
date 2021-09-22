/**
 * 
 */
package com.tony.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

/**
 * @author  tony
 * @version 1.0
 * @date    2021-09-17 15:57:31
 *
 */

public class ServerApp {

  public static void main(String[] args) {
    EventLoopGroup mainGroup = new NioEventLoopGroup();
    EventLoopGroup workGroup = new NioEventLoopGroup();
    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(mainGroup, workGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 128)
        .childOption(ChannelOption.SO_KEEPALIVE, true).childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
              @Override
              public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                ByteBuf bb = (ByteBuf) msg;
                System.out.println("收到客户端 " + ctx.channel().remoteAddress() + " 的消息：" + bb.toString(CharsetUtil.UTF_8));
              }

              @Override
              public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                ctx.writeAndFlush(Unpooled.copiedBuffer("消息已收到！", CharsetUtil.UTF_8));
              }

              @Override
              public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                ctx.close();
              }
            });
          }
        });
    System.out.println("服务端已启动。。。");
    try {
      ChannelFuture future = bootstrap.bind(8888).sync();
      future.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      mainGroup.shutdownGracefully();
      workGroup.shutdownGracefully();
    }
  }

}
