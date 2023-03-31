package cn.zhanghui.demo.daily.netty.heartbeat;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
/*
 * 服务端业务处理类
 */
public class ServerHeartBeatHandler extends ChannelHandlerAdapter {
	private static HashMap<String, String> AUTH_IP_MAP = new HashMap<String, String>();
	private static final String SUCCESS_KEY = "auth_success_key";
	
	static {
		AUTH_IP_MAP.put("192.168.1.200", "1234");
	}
	
	private boolean auth(ChannelHandlerContext ctx, Object msg) {
		 System.out.println(msg);
		 String[] ret = ((String)msg).split(",");
		 String auth = AUTH_IP_MAP.get(ret[0]);
		 if(auth != null && auth.equals(ret[1])) {
			 ctx.writeAndFlush(SUCCESS_KEY);
			 return true;
		 }else {
			 ctx.writeAndFlush("auth failure!").addListener(ChannelFutureListener.CLOSE);
			 return false;
		 }
		 
	}
    //管道激活时
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}
    //有消息通知时
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof String) {
			auth(ctx, msg);
		}else if(msg instanceof RequestInfo) {
			RequestInfo info = (RequestInfo)msg;
			System.out.println(info);
			ctx.writeAndFlush("info received!");
		}else {
			ctx.writeAndFlush("connect failure!").addListener(ChannelFutureListener.CLOSE);
		}
	}
    
	//捕获到异常时
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
    
}
