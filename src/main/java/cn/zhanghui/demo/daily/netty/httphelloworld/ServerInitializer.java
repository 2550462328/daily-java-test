package cn.zhanghui.demo.daily.netty.httphelloworld;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    
	private final SslContext sslCtx;
	public ServerInitializer(SslContext sslCtx) {
		this.sslCtx = sslCtx;
	}
	@Override
	protected void initChannel(SocketChannel sc) throws Exception {
		ChannelPipeline p = sc.pipeline();
		if(sslCtx != null) {
			p.addLast(sslCtx.newHandler(sc.alloc()));
		}
		p.addLast(new HttpServerCodec());
		p.addLast(new ServerHandler());
	}
}
