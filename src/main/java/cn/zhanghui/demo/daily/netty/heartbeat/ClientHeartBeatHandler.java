package cn.zhanghui.demo.daily.netty.heartbeat;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ClientHeartBeatHandler extends ChannelHandlerAdapter  {
    
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private ScheduledFuture<?> heartBeat;
	
	//主动向服务器发送认证信息
	private InetAddress addr;
	
	private static final String SUCCESS_KEY = "auth_success_key";
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			if (msg instanceof String) {
				String ret = (String) msg;
				if (SUCCESS_KEY.equals(ret)) {
					//握手成功，主动发送心跳消息
					this.heartBeat = this.scheduler.scheduleWithFixedDelay(new HeartBeatTask(ctx), 0, 2,
							TimeUnit.SECONDS);
					System.out.println(msg);
				} else {
					System.out.println(msg);
				}
			} 
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if(heartBeat != null) {
			heartBeat.cancel(true);
			heartBeat = null;
		}
		ctx.close();
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("通道激活");
		addr = InetAddress.getLocalHost();
		String ip = addr.getHostAddress();
		String key = "1234";
		//证书
		String auth = ip+","+"key";
		ctx.writeAndFlush(auth);
	}
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.out.println("读数据完毕");
	}
    
	public class HeartBeatTask implements Runnable{
		private final ChannelHandlerContext ctx;
        public HeartBeatTask(final ChannelHandlerContext ctx) {
        	this.ctx = ctx;
        }
		@Override
		public void run() {
			try {
				RequestInfo info = new RequestInfo();
				//ip
				info.setIp(addr.getHostAddress());
				Sigar sigar = new Sigar();
				//cpu 
				CpuPerc cpuPrec = sigar.getCpuPerc();
				HashMap<String, Object> cpuPrecMap = new HashMap<String, Object>();
				cpuPrecMap.put("combined", cpuPrec.getCombined());
				cpuPrecMap.put("user", cpuPrec.getUser());
				cpuPrecMap.put("sys", cpuPrec.getSys());
				cpuPrecMap.put("wait", cpuPrec.getWait());
				cpuPrecMap.put("idle", cpuPrec.getIdle());
				//memory
				Mem mem = sigar.getMem();
				HashMap<String, Object> memMap = new HashMap<String, Object>();
				memMap.put("total", mem.getTotal());
				memMap.put("used", mem.getUsed());
				memMap.put("free", mem.getFree());
				info.setMemoryMap(memMap);
				info.setCpuPercMap(cpuPrecMap);
				ctx.writeAndFlush(info);
			} catch (SigarException e) {
				e.printStackTrace();
			}
		}
		
	}
}
