package cn.zhanghui.demo.daily.base.thread.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * @ClassName: LockSynchronized.java
 * @Description: 实现lock基于Condition的多路通知（选择通知）
 *               两个线程执行condition1.await(),两个线程执行condition2.await() 
 * @author: ZhangHui
 * @date: 2019年5月22日 下午3:05:14
 */
public class LockSynchronized {
	private Lock lock = new ReentrantLock();
	private Condition condition1 = lock.newCondition();
	private Condition condition2 = lock.newCondition();
	public void methodA()  {
		try {
			lock.lock();
			System.out.println(Thread.currentThread().getName() + "获取到lock锁" + System.currentTimeMillis());
			condition1.await();
			System.out.println(Thread.currentThread().getName() + "等待结束，继续执行" + System.currentTimeMillis());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	
	public void methodB() {
		try {
			lock.lock();
			System.out.println(Thread.currentThread().getName() + "获取到lock锁" + System.currentTimeMillis());
			condition1.await();
			System.out.println(Thread.currentThread().getName() + "等待结束，继续执行" + System.currentTimeMillis());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	
	public void methodC()  {
		try {
			lock.lock();
			System.out.println(Thread.currentThread().getName() + "获取到lock锁" + System.currentTimeMillis());
			condition2.await();
			System.out.println(Thread.currentThread().getName() + "等待结束，继续执行" + System.currentTimeMillis());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	
	public void methodD()  {
		try {
			lock.lock();
			System.out.println(Thread.currentThread().getName() + "获取到lock锁" + System.currentTimeMillis());
			condition2.await();
			System.out.println(Thread.currentThread().getName() + "等待结束，继续执行" + System.currentTimeMillis());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	
	public void methodE()  {
		try {
			lock.lock();
			System.out.println(Thread.currentThread().getName() + "获取到lock锁" + System.currentTimeMillis());
			condition1.signal();
			System.out.println(Thread.currentThread().getName() + "等待结束，继续执行" + System.currentTimeMillis());
		} finally {
			lock.unlock();
		}
	}
	
	public void methodF()  {
		try {
			lock.lock();
			System.out.println(Thread.currentThread().getName() + "获取到lock锁" + System.currentTimeMillis());
			condition2.signal();
			System.out.println(Thread.currentThread().getName() + "等待结束，继续执行" + System.currentTimeMillis());
		} finally {
			lock.unlock();
		}
	}
	
	public static void main(String[] args) {
		LockSynchronized lockSynchronized = new LockSynchronized();
		Thread threadA1 = new Thread(new Runnable() {
			@Override
			public void run() {
				lockSynchronized.methodA();
			}
		});
		threadA1.setName("threadA1");
		Thread threadA2 = new Thread(new Runnable() {
			@Override
			public void run() {
				lockSynchronized.methodB();
			}
		});
		threadA2.setName("threadA2");
		Thread threadB1 = new Thread(new Runnable() {
			@Override
			public void run() {
				lockSynchronized.methodC();
			}
		});
		threadB1.setName("threadB1");
		Thread threadB2 = new Thread(new Runnable() {
			@Override
			public void run() {
				lockSynchronized.methodD();
			}
		});
		threadB2.setName("threadB2");
		
		Thread threadC = new Thread(new Runnable() {
			@Override
			public void run() {
				lockSynchronized.methodE();
			}
		});
		threadC.setName("threadC");

		Thread threadD = new Thread(new Runnable() {
			@Override
			public void run() {
				lockSynchronized.methodF();
			}
		});
		threadD.setName("threadD");
		threadA1.start();
		threadA2.start();
		threadB1.start();
		threadB2.start();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		threadC.start();
		threadD.start();
	}
}
