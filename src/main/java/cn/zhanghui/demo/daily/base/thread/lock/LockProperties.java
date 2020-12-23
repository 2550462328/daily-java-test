package cn.zhanghui.demo.daily.base.thread.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName: LockProperties.java
 * @Description: 测试锁的方法和属性
 * @author: ZhangHui
 * @date: 2019年5月22日 下午5:22:08
 */
public class LockProperties {
	private ReentrantLock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();

	public void methodA() {
		try {
			//lock.lock()在线程被interupt的时候会正常执行完，lock.lockInterruptibly则抛出异常。
			lock.lockInterruptibly();
			// lock.getHoldCount()获取当前线程保持此锁的个数，也就是lock的调用次数
			System.out.println(Thread.currentThread().getName() + "：lock.getHoldCount() = " + lock.getHoldCount());
//			 Thread.sleep(2000);
			for(int i =0; i < Integer.MAX_VALUE/1000; i++) {
				Math.random();
			}
			// 获取正在等待此锁释放的线程估计数
			System.out.println(Thread.currentThread().getName() + "：lock.getQueueLength() = " + lock.getQueueLength());
			///测试出现异常会不会释放锁，结果是不会	
			// throw new RuntimeException();
			// Reentrantlock可重入锁，在保持锁定的情况下，可以重入此线程其他需要获取锁定的方法
			// this.methodB();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if(lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
			System.out.println(Thread.currentThread().getName() + "：lock has released");
		}
	}

	public void methodB() {
		try {
			lock.lock();
			//lock.hasQueuedThreads()：查询是否有线程正在等待获取此锁定
			System.out.println(Thread.currentThread().getName() + "：lock.hasQueuedThreads() = " + lock.hasQueuedThreads());
			// lock.getHoldCount()获取当前线程保持此锁的个数，也就是lock的调用次数
			System.out.println(Thread.currentThread().getName() + "：lock.getHoldCount() = " + lock.getHoldCount());
			//lock.isFair()：查询当前锁是否公平
			System.out.println(Thread.currentThread().getName() + "：lock.isFair() = " + lock.isFair());
			//lock.isHeldByCurrentThread()：查询锁是否被当前线程持有
			System.out.println(Thread.currentThread().getName() + "：lock.isHeldByCurrentThread() = " + lock.isHeldByCurrentThread());
			//lock.isLocked：查询锁是否被任意线程保持（是否正在被使用）
			System.out.println(Thread.currentThread().getName() + "：lock.isLocked() = " + lock.isLocked());
		} finally {
			if(lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
			System.out.println(Thread.currentThread().getName() + "：lock has released");
		}
	}

	public void methodC() {
		try {
			lock.lock();
			condition.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if(lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}
	
	public void methodD() {
		try {
			lock.lock();
			System.out.println("lock.hasWaiters() = " + lock.hasWaiters(condition));
			System.out.println("lock.getWaitQueueLength(condition) = " + 
			    lock.getWaitQueueLength(condition));
			condition.signalAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
		
	}
	public static void main(String[] args) {
		LockProperties lockProperties = new LockProperties();
//		lockProperties.methodA();
		Thread threadA = new Thread(new Runnable() {
			@Override
			public void run() {
				lockProperties.methodA();
			}
		});
		threadA.setName("threadA");
		threadA.start();
		threadA.interrupt();
//		Thread threadB = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				lockProperties.methodB();
//			}
//		});
//		threadB.setName("threadB");
//		threadB.start();
		
//		Thread[] thread = new Thread[10];
//		for(int i = 0; i < 10; i++) {
//			thread[i] = new Thread(new Runnable() {
//				@Override
//				public void run() {
//					lockProperties.methodC();
//				}
//			});
//		}
//		for(int i = 0; i < 10; i++) {
//			thread[i].start();
//		}
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		lockProperties.methodD();
	}
}
