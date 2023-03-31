package cn.zhanghui.demo.daily.base.thread.lock.aqs;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName CountDownLatch.class
 * @Description: CountDownLatch的测试
 * 模拟场景：一群人出发去旅游，需要等待所有人到齐，所有人到齐之后导演分发旅游说明书
 * @Author: ZhangHui
 * @Date: 2020/3/11 @Version：1.0
 */
public class CountDownLatchTest {
    private ThreadPoolExecutor threadPool =
            new ThreadPoolExecutor(100, 1000, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(100));

    private CountDownLatch downLatch = new CountDownLatch(100);

    public static void main(String[] args) {
        new CountDownLatchTest().doDownLatch();
    }

    private void doDownLatch() {
        for (int i = 0; i < 100; i++) {
            Thread thread = new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "：我已到达...");
                downLatch.countDown();
            }, "thread" + i);
            threadPool.execute(thread);
        }
        try {
            downLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("我是导游，我来分发旅游说明书...");
        System.out.println("所有人已到达，我们出发吧！");
        threadPool.shutdown();
        while (true) {
            if (threadPool.isTerminated()) {
                System.out.println("线程池已关闭");
                break;
            }
        }
    }
}
