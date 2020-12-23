package cn.zhanghui.demo.daily.netty.helloworld;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
/*
 * 服务端业务处理类
 */
public class ServerHandler extends ChannelHandlerAdapter {
    //管道激活时
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}
    //有消息通知时
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf)msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req, "utf-8");
		System.out.println("server: " + body);
		
		//服务器端给客户端的响应
		String resp = "Hi Client!";
		ctx.writeAndFlush(Unpooled.copiedBuffer(resp.getBytes())); //返回消息也被封装成ByteBuf Unpooled是ByteBuf工具类
		//.addListener(ChannelFutureListener.CLOSE);
	}
    
	//捕获到异常时
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
    
}
