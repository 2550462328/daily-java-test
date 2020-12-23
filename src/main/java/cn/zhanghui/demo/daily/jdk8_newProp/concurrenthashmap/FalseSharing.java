package cn.zhanghui.demo.daily.jdk8_newProp.concurrenthashmap;

/**
 * 
 * @ClassName: FalseSharing.java
 * @Description: 这是一个实验
 *               使用填充法（填充7个long变量）避免伪共享的问题，测试结果看VolatileLong
 * @author: ZhangHui
 * @date: 2019年11月4日 下午10:25:50
 */
public final class FalseSharing implements Runnable {
	public final static int NUM_THREADS = 4; // change
	public final static long ITERATIONS = 500L * 1000L * 1000L;
	private final int arrayIndex;

	private static VolatileLong[] longs = new VolatileLong[NUM_THREADS];
	static {
		for (int i = 0; i < longs.length; i++) {
			longs[i] = new VolatileLong();
		}
	}

	public FalseSharing(final int arrayIndex) {
		this.arrayIndex = arrayIndex;
	}

	public static void main(final String[] args) throws Exception {
		final long start = System.nanoTime();
		runTest();
		System.out.println("duration = " + (System.nanoTime() - start));
	}

	private static void runTest() throws InterruptedException {
		Thread[] threads = new Thread[NUM_THREADS];

		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new FalseSharing(i));
		}

		for (Thread t : threads) {
			t.start();
		}

		for (Thread t : threads) {
			t.join();
		}
	}

	public void run() {
		long i = ITERATIONS + 1;
		while (0 != --i) {
			longs[arrayIndex].value = i;
		}
	}

	public final static class VolatileLong {  // 注释前运行时间19973627800
		public volatile long value = 0L;
//		public long p1, p2, p3, p4, p5, p6; // 注释后运行时间 53126964800
	}
}