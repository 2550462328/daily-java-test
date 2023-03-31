package cn.zhanghui.demo.daily.base.thread.base;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName: ThreadTest.java
 * NEW：创建了还未运行的线程状态
 * RUNNABLE：正在运行中的线程状态
 * BLOCKED：等待获取lock的线程状态
 * TIMED_WAITING：有时间上限的等待状态，时间到了，自动执行
 * 一般在Thread.sleep(long mills)或者Object.wait(long mills)等有时间上限的
 * WAITING：在执行了Object.wait()或者thread.join()或者LockSupport.park()的线程
 * TERMINATED：线程被执行完（或者被中断）等被销毁时的状态
 * @Description: 验证线程中的状态
 * @author: ZhangHui
 * @date: 2019年5月24日 下午3:06:35
 */
public class ThreadTest {

    static Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread thread = Thread.currentThread();
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                thread.interrupt();
            } catch (InterruptedException e) {

            }
        }).start();

        synchronized (lock) {
            System.out.println(thread.isInterrupted());
            lock.wait();
            System.out.println(thread.isInterrupted());
            System.out.println("waiting end!");
        }
    }
}
