package cn.zhanghui.demo.daily.base.thread.designmodel.providerconsumer;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class Consumer implements Runnable {
    private BlockingQueue<Data> queue;

    public Consumer(BlockingQueue<Data> queue) {
        this.queue = queue;
    }

    private Random r = new Random();

    @Override
    public void run() {
        while (true) {
            try {
                Data data = this.queue.take();
                Thread.sleep(r.nextInt(1000));
                System.out.println("当前消费线程:" + Thread.currentThread().getName() + ",消费成功，消费线程id为" + data.getId() + ",消费线程数据为" + data.getName());
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
