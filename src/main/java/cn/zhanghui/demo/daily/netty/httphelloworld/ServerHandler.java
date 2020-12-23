package cn.zhanghui.demo.daily.netty.httphelloworld;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
/*
 * 服务端业务处理类
 */
public class ServerHandler extends ChannelHandlerAdapter {
	
	private static final byte[] content = {'H','e','l','l','o'};
	
    //管道激活时
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}
	
    @Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    	ctx.flush();
	}

	//有消息通知时
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof HttpRequest) {
			HttpRequest req = (HttpRequest)msg;
			if(HttpHeaderUtil.is100ContinueExpected(req)) {
				ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
			}
			boolean keepAlive = HttpHeaderUtil.isKeepAlive(req);
			FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,Unpooled.wrappedBuffer(content));
			resp.headers().set("Content-Type", "text/plain");
			resp.headers().setInt("Content-Length", resp.content().readableBytes());
			if(!keepAlive) {
				ctx.write(resp).addListener(ChannelFutureListener.CLOSE);
			}else {
				resp.headers().set("Connection", HttpHeaderValues.KEEP_ALIVE);
				ctx.write(resp);
			}
		}
	}
    
	//捕获到异常时
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
    
}
