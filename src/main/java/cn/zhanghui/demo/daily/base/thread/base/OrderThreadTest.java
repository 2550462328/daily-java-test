package cn.zhanghui.demo.daily.base.thread.base;

/**
 * @ClassName: OrderThread.java
 * @Description: 使用程序控制线程的有序性
 * @author: ZhangHui
 * @date: 2019年5月24日 下午3:42:16
 */
public class OrderThreadTest {
	public static void main(String[] args) {
		Object lock = new Object();
		OrderThread thread1 = new OrderThread(lock, "i am thread1", 1);
		OrderThread thread2 = new OrderThread(lock, "i am thread2", 2);
		OrderThread thread3 = new OrderThread(lock, "i am thread3", 3);
		OrderThread thread4 = new OrderThread(lock, "i am thread4", 0);
		
		thread1.start();
		thread2.start();
		thread3.start();
		thread4.start();
	}
}

class OrderThread extends Thread {
	private Object lock; // 对象锁
	private String message; // 输出语句
	private int orderNumber; // 序号
	private volatile static int controlNum = 1; // 控制序号的选择
	private int printCount = 0; // 控制循环的结束

	public OrderThread(Object lock, String message, int orderNumber) {
		super();
		this.lock = lock;
		this.message = message;
		this.orderNumber = orderNumber;
	}

	@Override
	public void run() {
		try {
			System.out.println(Thread.currentThread().getName() + "正在等待获取");
			synchronized (lock) {
				while (true) {
					if (controlNum % 4 == orderNumber) {
						System.out.println(Thread.currentThread().getName() + "输出：" + message);
						// 可以做业务逻辑
						controlNum++;
						printCount++;
						lock.notifyAll();
						if (printCount == 3) {
							break;
						}
					} else {
						lock.wait();
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}