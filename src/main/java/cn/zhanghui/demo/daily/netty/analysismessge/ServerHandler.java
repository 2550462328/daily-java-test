package cn.zhanghui.demo.daily.netty.analysismessge;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
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
		String req = (String)msg;
		System.out.println("server:"+req);
		//服务器端给客户端的响应
		String resp = "我是响应数据$_";
		
		ctx.writeAndFlush(Unpooled.copiedBuffer(resp.getBytes())).addListener(ChannelFutureListener.CLOSE); //返回消息也被封装成ByteBuf Unpooled是ByteBuf工具类
		
	}
    
	//捕获到异常时
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
    
}
