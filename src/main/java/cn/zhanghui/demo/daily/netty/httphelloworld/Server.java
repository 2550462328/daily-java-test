package cn.zhanghui.demo.daily.netty.httphelloworld;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/*
 * 使用netty做服务端模拟http请求
 */
public class Server {
	
	static final boolean SSL = System.getProperty("ssl") != null;
	static final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "8443":"8080"));
	
    public static void main(String[] args) throws Exception {
    	//配置ssl
    	final SslContext sslctx;
    	if(SSL) {
    		SelfSignedCertificate ssc = new SelfSignedCertificate();
    		sslctx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
    	}else {
    		sslctx = null;
    	}
    	//配置服务器
		EventLoopGroup pGroup = new NioEventLoopGroup(); //用来处理服务器端接受客户端连接的
		EventLoopGroup cGroup = new NioEventLoopGroup(); //用来进行网络读写的
		
		try {
			ServerBootstrap b = new ServerBootstrap(); //创建辅助工具类，用于服务器端通道的一系列配置
			b.group(pGroup, cGroup) //绑定两个线程组
					.channel(NioServerSocketChannel.class) //指定NIO的模式
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new ServerInitializer(sslctx));
			Channel ch = b.bind(PORT).sync().channel();
			System.out.println("Open your web brower and navigate to " + (SSL ? "https" : "http:") + "127.0.0.1:" + PORT + "/");
			ch.closeFuture().sync();
		} finally {
			pGroup.shutdownGracefully();
			cGroup.shutdownGracefully();
		}
		
	}
}
