package cn.zhanghui.demo.daily.base.thread.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName: LockAlternate.java
 * @Description: 使用lock实现两个线程相互通知，交叉进行
 * @author: ZhangHui
 * @date: 2019年5月22日 下午3:07:42
 */
public class LockAlternate {
	private Lock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();

	private boolean flag = false;

	public void get() {
		lock.lock();
		try {
			while (flag == true) {
				condition.await();
			}
			System.out.println("get");
			flag = true;
			condition.signalAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
			System.out.println("get锁被释放");
		}
	}

	public void set() {
		lock.lock();
		try {
			while (flag == false) {
				condition.await();
			}
			System.out.println("set");
			flag = false;
			condition.signalAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
			System.out.println("set锁被释放");
		}
	}

	public static void main(String[] args) {
		LockAlternate lockAlternate = new LockAlternate();
		Thread threadA = new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i = 0; i < Integer.MAX_VALUE; i++) {
					lockAlternate.get();
				}
			}
		});
		Thread threadB = new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i = 0; i < Integer.MAX_VALUE; i++) {
					lockAlternate.set();
				}
			}
		});
		Thread[] threadArr = new Thread[10];
		Thread[] threadBrr = new Thread[10];
		for(int i = 0; i < 10; i++) {
			threadArr[i] = threadA;
			threadBrr[i] = threadB;
			
			threadArr[i].start();
			threadBrr[i].start();
		}
	}
}
