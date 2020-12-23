package cn.zhanghui.demo.daily.netty.helloworld;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/*
 * 服务端
 */
public class Server {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup pGroup = new NioEventLoopGroup(); // 用来处理服务器端接受客户端连接的
        EventLoopGroup cGroup = new NioEventLoopGroup(); // 用来进行网络读写的
        ServerBootstrap b = new ServerBootstrap(); // 创建辅助工具类，用于服务器端通道的一系列配置
        b.group(pGroup, cGroup) // 绑定两个线程组
            .channel(NioServerSocketChannel.class) // 指定NIO的模式
            .option(ChannelOption.SO_BACKLOG, 1024) // 指定tcp的缓冲区
            .option(ChannelOption.SO_SNDBUF, 32 * 1024) // 设置发送的缓冲大小
            .option(ChannelOption.SO_RCVBUF, 32 * 1024) // 设置接收的缓冲大小
            .option(ChannelOption.SO_KEEPALIVE, true) // 保持连接
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel sc) throws Exception {
                    sc.pipeline().addLast(new ServerHandler()); // 在这里设置具体数据接收方法的处理
                }
            });

        ChannelFuture cf1 = b.bind(8765).sync(); // 进行绑定 sync 表示进行阻塞

        cf1.channel().closeFuture().sync(); // 优雅关机 服务端一直在执行 阻塞着等待关机
        // Thread.sleep(Integer.MAX_VALUE) 线程无限休眠

        pGroup.shutdownGracefully();
        cGroup.shutdownGracefully();
    }
}
