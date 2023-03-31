package cn.zhanghui.demo.daily.base.thread.lock.aqs.cyclicbarrier;

import java.util.concurrent.*;

/**
 * @ClassName
 * @Description: CycleBarrier的测试
 * 模拟场景：一群人出发去旅游，需要等待所有人到齐，所有人到齐之后导演分发旅游说明书
 * @Author: ZhangHui
 * @Date: 2020/3/11
 * @Version：1.0
 */
public class CyclicBarrierTest {

    private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();

    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(100, 1000, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(100));

    private CyclicBarrier barrier = new CyclicBarrier(100, new FinalTask());

    public static void main(String[] args) {
        new CyclicBarrierTest().doBarrier();
    }

    private void doBarrier() {
        for (int i = 0; i < 100; i++) {
            Thread thread = new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "：我已到达...");
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + "：去出发旅游咯...");
            }, "thread" + i);
            threadPool.execute(thread);
        }
        threadPool.shutdown();
        while (true) {
            if (threadPool.isTerminated()) {
                System.out.println("线程池已关闭");
                break;
            }
        }
    }
}

/**
 * 执行解除屏障前的操作，当前线程由最后一个进入屏障的线程执行！
 *
 * @author ZhangHui
 * @date 2020/3/11
 * @return
 */
class FinalTask implements Runnable {
    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "：我是导游，我来分发旅游说明书了...");
    }
}