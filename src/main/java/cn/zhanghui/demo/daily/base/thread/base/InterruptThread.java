package cn.zhanghui.demo.daily.base.thread.base;

/**
 * @ClassName: InterruptThread.java
 * @Description: 当线程被interrupt的时候不会停止运行，只是将终端状态标记成true
 * thread.interrupted和isInterrupted的区别就是在执行完interrupted后会将中断状态改成false
 * 下面是一个经典的停止thread的方式之一
 * @author: ZhangHui
 * @date: 2019年10月28日 下午12:36:39
 */
public class InterruptThread {
	public static void main(String[] args) {
		Thread thread = new Thread(new Mythread());
		thread.start();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Thread.yield();
		thread.interrupt();
		System.out.println(Thread.currentThread().getName() + ":" + thread.isInterrupted());
	}
}

 class Mythread implements Runnable  {
	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
//			try {
//				Thread.sleep(1000);
//			    //当线程在sleep、wait、join的时候被中断，会抛出InterruptedExceptionn，同时将thread.isInterrupted置为false，需要在catch再次执行interrupt()
//				// Thread.sleep(10000);  
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			System.out.println(Thread.currentThread().getName() + ":" + Thread.currentThread().isInterrupted());
			System.out.println("i am a thread, i am running so happy!");
		}
	}
}