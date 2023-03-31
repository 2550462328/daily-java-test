package cn.zhanghui.demo.daily.netty.analysismessge;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        //设置分隔符
                        ByteBuf buf = Unpooled.copiedBuffer("$_".getBytes());
                        sc.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, buf));
                        //设置字符串类型的编码 将Buf类型转换成String类型
                        sc.pipeline().addLast(new StringDecoder());
                        sc.pipeline().addLast(new ClientHandler());
                    }
                });

        ChannelFuture cf1 = b.connect("127.0.0.1", 8765).sync();
        cf1.channel().writeAndFlush(Unpooled.wrappedBuffer("zhang$_".getBytes()));
        cf1.channel().writeAndFlush(Unpooled.wrappedBuffer("hui$_".getBytes()));
        cf1.channel().writeAndFlush(Unpooled.wrappedBuffer("zuishuai$_".getBytes()));

        cf1.channel().closeFuture().sync();
        group.shutdownGracefully();
    }


}
