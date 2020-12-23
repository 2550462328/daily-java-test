package cn.zhanghui.demo.daily.base.thread.lock;
/**
 * 
 * @ClassName: DeadLock.java
 * @Description: 模拟死锁的场景
 * @author: ZhangHui
 * @date: 2019年5月20日 下午4:48:33
 */
public class DeadLock {
	public static Object lock1 = new Object();
	public static Object lock2 = new Object();
	
	public static void main(String[] args) {
		Thread threadA = new Thread(new MyThread1());
		threadA.setName("threadA");
		threadA.start();
		Thread threadB = new Thread(new MyThread2());
		threadB.setName("threadB");
		threadB.start();
	}
	
}
class MyThread1 extends Thread {
	@Override
	public void run() {
		synchronized (DeadLock.lock1) {
			System.out.println(Thread.currentThread().getName() + "线程获取到lock1");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (DeadLock.lock2) {
				System.out.println(Thread.currentThread().getName() + "线程获取到lock2");
			}
		}
	}
}

class MyThread2 extends Thread {
	@Override
	public void run() {
		synchronized (DeadLock.lock2) {
			System.out.println(Thread.currentThread().getName() + "线程获取到lock2");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (DeadLock.lock1) {
				System.out.println(Thread.currentThread().getName() + "线程获取到lock1");
			}
		}
	}
}
