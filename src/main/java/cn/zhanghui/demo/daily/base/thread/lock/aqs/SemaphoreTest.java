package cn.zhanghui.demo.daily.base.thread.lock.aqs;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName
 * @Description: Semephore的测试实例
 * 模拟场景：一个停车场只有3个停车位，现在有100辆骑车想停进来
 * @Author: ZhangHui
 * @Date: 2020/3/12 @Version：1.0
 */
public class SemaphoreTest {

    private Semaphore semaphore = new Semaphore(3,true);

    private ThreadPoolExecutor threadPool =
        new ThreadPoolExecutor(100, 1000, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000));

    public static void main(String[] args) {
        new SemaphoreTest().doPark();
    }

    private void doPark() {
        for (int i = 0; i < 100; i++) {
            threadPool.execute(() -> {
                String threadName = Thread.currentThread().getName();
                if("pool-1-thread-99".equals(threadName)){
                    System.out.println("semaphore.getQueueLength() = " + semaphore.getQueueLength());
                    System.out.println("semaphore.hasQueuedThreads() = " + semaphore.hasQueuedThreads());
                    System.out.println("semaphore.availablePermits() = " + semaphore.availablePermits());
                    System.out.println("semaphore.drainPermits() = " + semaphore.drainPermits());
                }
                try {
                    System.out.println(threadName + "：我在准备停车");
                    semaphore.acquire(1);
//                    semaphore.acquire();
//                    semaphore.acquireUninterruptibly(1);
//                    semaphore.tryAcquire();
//                    semaphore.tryAcquire(1);
//                    semaphore.tryAcquire(2,TimeUnit.SECONDS);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(threadName + "：我停进来了");
                System.out.println("");
                semaphore.release();
                System.out.println(threadName + "：我开走了");
            });
        }
        threadPool.shutdown();
        while (true) {
            if (threadPool.isTerminated()) {
                System.out.println("线程池关闭！");
                break;
            }
        }
    }
}
