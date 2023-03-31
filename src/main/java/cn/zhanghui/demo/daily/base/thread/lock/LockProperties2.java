package cn.zhanghui.demo.daily.base.thread.lock;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName: LockProperties.java
 * @Description: 测试锁的方法和属性
 * @author: ZhangHui
 * @date: 2019年5月22日 下午5:22:08
 */
public class LockProperties2 {
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void methodA() {
        try {
            lock.lock();
            System.out.println("threadA已获得lock");
            condition.await();
            Thread.sleep(15000);
            System.out.println("我准备释放锁了");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
            System.out.println(Thread.currentThread().getName() + "：lock has released");
        }
    }

    public void methodB() {
        try {
            lock.lock();
            System.out.println("condition await begin");
            //await期间如果被iterrupt会抛异常
//			condition.await();
            condition.awaitUninterruptibly();
            System.out.println("condition await end");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
            System.out.println(Thread.currentThread().getName() + "：lock has released");
        }
    }

    public void methodC() {
        try {
            if (lock.tryLock()) {
                System.out.println(Thread.currentThread().getName() + " get lock!");
            } else {
                System.out.println(Thread.currentThread().getName() + " do not get lock!");
            }
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
            System.out.println(Thread.currentThread().getName() + "：lock has released");
        }
    }

    public void methodD() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 10);
        lock.lock();
        try {
            System.out.println("i am waiting to obtain lock " + new Date().getSeconds());
            // 等待十秒后强行获得锁，中断原先获得锁的线程（不会恢复）
            condition.awaitUntil(calendar.getTime());
            System.out.println("i have get lock " + new Date().getSeconds());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
            System.out.println("lock has been released!");
        }
    }

    public static void main(String[] args) {
        LockProperties2 lockProperties = new LockProperties2();
//		Thread threadA = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				lockProperties.methodA();
//			}
//		});
//		threadA.setName("threadA");
//		threadA.start();
        Thread threadD = new Thread(new Runnable() {
            @Override
            public void run() {
                lockProperties.methodD();
            }
        });
        threadD.start();
        threadD.interrupt();
//		threadB.start();
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		threadB.interrupt();
//		threadC2.start();
    }
}
