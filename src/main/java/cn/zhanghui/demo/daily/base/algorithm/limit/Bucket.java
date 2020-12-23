package cn.zhanghui.demo.daily.base.algorithm.limit;

import com.google.common.util.concurrent.Monitor;
import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * @ClassName Bucket
 * @Description: 使用漏桶算法 + RateLimiter实现消费（读场景）限流
 * @Author: ZhangHui
 * @Date: 2019/7/9
 * @Version：1.0
 */
public class Bucket {
    // 定义桶的容器和最大值
    private final ConcurrentLinkedQueue<Integer> container = new ConcurrentLinkedQueue<Integer>();

    private final static int BUCKET_LIMIT = 5;

    // 无论多少个线程 1秒内只执行10次
    private final RateLimiter consumerRate = RateLimiter.create(10d);

    // 往桶中放数据的时候 确定没有超过当前的最大容量
    private Monitor offerMonitor = new Monitor();

    // 从桶中取数据的时候 桶中数据必须存在
    private Monitor pollMonitor = new Monitor();

    /**
     * 功能描述
     * 
     * @author 向桶中提交数据
     * @date 2019/7/9
     * @param data
     * @return
     */
    public void submit(Integer data){
        if(offerMonitor.enterIf(new Monitor.Guard(offerMonitor) {
            @Override
            public boolean isSatisfied() {
                return container.size() < BUCKET_LIMIT;
            }
        })){
            try {
                container.offer(data);
                System.out.println(Thread.currentThread().getName() + "is submiting data" + " container size is " + container.size());
            } finally {
                offerMonitor.leave();
            }
        }else{
            //这里时候采用降级策略了。消费速度跟不上产生速度时，而且桶满了，抛出异常
            //或者存入MQ DB等后续处理
            throw new IllegalStateException(Thread.currentThread().getName() + "The bucket is full Please latter try again");
        }
    }

    /**
     * 从桶中取走数据
     * @author ZhangHui
     * @date 2019/7/9
     * @return java.lang.Integer
     */
    public void takeThenConsumer(Consumer<Integer>consumer){
        if(pollMonitor.enterIf(new Monitor.Guard(pollMonitor) {
            @Override
            public boolean isSatisfied() {
                return !container.isEmpty();
            }
        })){
            try {
                System.out.println(Thread.currentThread().getName() + "is waiting " + consumerRate.acquire());
                Integer data = container.poll();
                //container.peek() 只是去取出来不会删掉
                consumer.accept(data);
            } finally {
                pollMonitor.leave();
            }
        }else{
            System.out.println(Thread.currentThread().getName() + "will consume data from MQ...");
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        final Bucket bucket = new Bucket();
        final AtomicInteger DATA_CREATOR = new AtomicInteger(0);

        //生产线程 10个线程 每秒提交 50个数据  1/0.2s*10=50个
        IntStream.range(0, 10).forEach(i -> {
            new Thread(() -> {
                for (; ; ) {
                    int data = DATA_CREATOR.incrementAndGet();
                    try {
                        bucket.submit(data);
                        TimeUnit.MILLISECONDS.sleep(200);
                    } catch (Exception e) {
                        //对submit时，如果桶满了可能会抛出异常
                        if (e instanceof IllegalStateException) {
                            System.out.println(e.getMessage());
                            //当满了后，生产线程就休眠1分钟
                            try {
                                TimeUnit.SECONDS.sleep(60);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        });


        //消费线程  采用RateLimiter每秒处理10个  综合的比率是5:1
        IntStream.range(0, 10).forEach(i -> {
            new Thread(
                    () -> {
                        for (; ; ) {
                            bucket.takeThenConsumer(x -> {
                                System.out.println(Thread.currentThread().getName() +  "is consuming");
                            });
                        }
                    }
            ).start();
        });
    }
}
