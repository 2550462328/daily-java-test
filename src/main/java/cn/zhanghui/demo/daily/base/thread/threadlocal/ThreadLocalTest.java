package cn.zhanghui.demo.daily.base.thread.threadlocal;

import java.util.concurrent.CyclicBarrier;

/**
 * 一个简单的官方ThreadLocal的运用
 * 在每个线程里面为ThreadLocal set的值，只有当前线程可以使用
 * @ClassName: TreadLocalTest.java
 * @Description: 该类的功能描述
 * @author: ZhangHui
 * @date: 2019年12月12日 下午2:52:00
 */
public class ThreadLocalTest {
	private ThreadLocal<String> threadLocal = new ThreadLocal<>();
	
	public static void main(String[] args) {
		CyclicBarrier cyclicBarrier = new CyclicBarrier(10);
		for (int i = 0; i < 10; i++) {
			TestThread testThread = new ThreadLocalTest().new TestThread(cyclicBarrier);
			testThread.start();
			// 等待线程执行完毕
			testThread.yield();
		}
	}

	class TestThread extends Thread {
		private CyclicBarrier cyclicBarrier;
		public TestThread(CyclicBarrier cyclicBarrier) {
			this.cyclicBarrier = cyclicBarrier;
		}

		@Override
		public void run() {
			try {
				// 保证10个线程同时启动
				cyclicBarrier.await();
				threadLocal.set(Thread.currentThread().getName());
				System.out.println(Thread.currentThread().getName() + ": " +  threadLocal.get());
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}
}
