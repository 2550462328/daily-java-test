package cn.zhanghui.demo.daily.base.thread.designmodel.providerconsumer;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Provider implements Runnable {
    //共享缓存区
	private BlockingQueue<Data> queue;
	//多线程是否启动变量，有强制性从内存中刷新的功能，即时返回线程的状态
	private volatile boolean isRunning = true;
	//id生成器
	private static AtomicInteger count = new AtomicInteger();
	//随机对象
	private static Random r = new Random();
	
	public Provider(BlockingQueue<Data> queue) {
		this.queue = queue;
	}
	@Override
	public void run() {
		while(isRunning) {
			try {
				//随机休眠0到1秒，表示获取数据的时间
				Thread.sleep(r.nextInt(1000));
				//获取的数据进行累积
				int id = count.incrementAndGet();
				//比如通过getData获取数据
				Data data = new Data(String.valueOf(id),"数据"+id);
				System.out.println("当前线程"+Thread.currentThread().getName()+",加载到公共缓冲区中,id为" + data.getId()+ ",数据为"+data.getName());
				if(!this.queue.offer(data, 2, TimeUnit.SECONDS)) {
					System.out.println("提交缓冲区失败!");
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	public void stop() {
		this.isRunning = false;
	}
}
