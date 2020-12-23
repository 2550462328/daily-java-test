package cn.zhanghui.demo.daily.base.thread.lock.aqs;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @ClassName: ReadWriteLockTest.java
 * @Description: 读写锁的测试
 * @author: ZhangHui
 * @date: 2019年5月23日 下午4:38:15
 */
public class ReadWriteLockTest {
	
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	public void write() {
		try {
			System.out.println(Thread.currentThread().getName() + "等待获取");
			lock.writeLock().lock();
			System.out.println(Thread.currentThread().getName() + " 已获取：" + System.currentTimeMillis());
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.writeLock().unlock();
			System.out.println(Thread.currentThread().getName() + " 已释放锁");
		}
	}

	public void read() {
		try {
			System.out.println(Thread.currentThread().getName() + " 等待获取");
			lock.readLock().lock();
			System.out.println(Thread.currentThread().getName() + " 已获取：" + System.currentTimeMillis());
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.readLock().unlock();
			System.out.println(Thread.currentThread().getName() + " 已释放锁");
		}
	}
	public static void main(String[] args) {
		ReadWriteLockTest readWriteLockTest = new ReadWriteLockTest();
		//在这里可以切换读写线程，测试读读、读写、写读和写写场景下
		Thread readThread = new Thread(new WriteThread(readWriteLockTest));
		Thread writeThread = new Thread(new WriteThread(readWriteLockTest));
		readThread.setName("readThread");
		writeThread.setName("writeThread");
		readThread.start();
		writeThread.start();
	}
}

class ReadThread extends Thread {
	private ReadWriteLockTest readWriteLockTest;

	public ReadThread(ReadWriteLockTest readWriteLockTest) {
		this.readWriteLockTest = readWriteLockTest;
	}
	@Override
	public void run() {
		readWriteLockTest.read();
	}
}

class WriteThread extends Thread {
	private ReadWriteLockTest readWriteLockTest;
	public WriteThread(ReadWriteLockTest readWriteLockTest) {
		this.readWriteLockTest = readWriteLockTest;
	}
	@Override
	public void run() {
		readWriteLockTest.write();
	}
}
