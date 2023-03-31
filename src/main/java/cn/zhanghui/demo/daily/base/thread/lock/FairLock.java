package cn.zhanghui.demo.daily.base.thread.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName: FairLock.java
 * @Description: 测试lock的公平锁和非公平锁
 * @author: ZhangHui
 * @date: 2019年5月22日 下午5:04:28
 */
public class FairLock {
    //ReentrantLock的构造方法可以实现公平锁和非公平锁
    private Lock lock = new ReentrantLock(false);

    public static void main(String[] args) {
        FairLock fairLock = new FairLock();
        Thread[] thread = new Thread[10];
        for (int i = 0; i < 10; i++) {
            thread[i] = new Thread(new FairLock().new ThreadA(fairLock));
            thread[i].setName("Thread" + i);
        }
        for (int i = 0; i < 10; i++) {
            thread[i].start();
        }
    }

    class ThreadA implements Runnable {

        private FairLock fairLock;

        public ThreadA(FairLock fairLock) {
            this.fairLock = fairLock;
        }

        @Override
        public void run() {
            try {
                System.out.println(Thread.currentThread().getName() + "运行了");
                fairLock.lock.lock();
                System.out.println(Thread.currentThread().getName() + "get lock");
            } finally {
                fairLock.lock.unlock();
            }
        }

    }
}

