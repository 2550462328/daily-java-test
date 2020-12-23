package cn.zhanghui.demo.daily.netty.httpfile;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/*
 * 使用netty做http协议文件处理
 */
public class Server {

    private static final String DEFAULT_URL = "/sources/";

    public void run(final int port, final String url) throws InterruptedException {
        // 配置服务器
        EventLoopGroup pGroup = new NioEventLoopGroup(); // 用来处理服务器端接受客户端连接的
        EventLoopGroup cGroup = new NioEventLoopGroup(); // 用来进行网络读写的

        try {
            ServerBootstrap b = new ServerBootstrap(); // 创建辅助工具类，用于服务器端通道的一系列配置
            b.group(pGroup, cGroup) // 绑定两个线程组
                    .channel(NioServerSocketChannel.class) // 指定NIO的模式
                    .handler(new LoggingHandler(LogLevel.INFO)).
                    childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) {
                            // 加入http解码器
                            sc.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                            // 加入ObjectAggregator解码器,作用是将多个消息转换成单一的FullHttpRequest或着FullHttpResponse
                            sc.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
                            // 加入http编码器
                            sc.pipeline().addLast("http-encoder", new HttpRequestEncoder());
                            // 加入chunk,主要作用是支持异步发送的码流（大文件传输），但不专用过多的内存，防止java内存溢出
                            sc.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                            sc.pipeline().addLast("file-server", new ServerHandler(url));
                        }
                    });
            ChannelFuture ch = b.bind("192.168.7.67", port).sync();
            System.out.println("Http文件服务器启动，网址是：" + "http://192.168.7.67:" + port + url);
            ch.channel().closeFuture().sync();
        } finally {
            pGroup.shutdownGracefully();
            cGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int port = 8765;
        String url = DEFAULT_URL;
        new Server().run(port, url);
    }
}
