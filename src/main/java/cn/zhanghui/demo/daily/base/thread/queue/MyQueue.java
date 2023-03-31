package cn.zhanghui.demo.daily.base.thread.queue;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName: MyQueue.java
 * @Description: 模拟阻塞Queue
 * @author: ZhangHui
 * @date: 2019年11月28日 上午11:08:29
 */
public class MyQueue {
    private LinkedList<Object> list = new LinkedList<Object>();

    private AtomicInteger count = new AtomicInteger(0);

    private final int min = 0;
    private final int max;

    public MyQueue(int size) {
        this.max = size;
    }

    private final Object lock = new Object();

    public void put(Object obj) {
        synchronized (lock) {
            while (count.get() == this.max) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            list.add(obj);
            count.incrementAndGet();
            lock.notify();
            System.out.println("新加入的元素为:" + obj);
        }
    }

    public Object take() {
        Object obj = null;
        synchronized (lock) {
            while (count.get() == min) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            obj = list.removeFirst();
            count.decrementAndGet();
            lock.notify();
            System.out.println("取出的元素为:" + obj);
            return obj;
        }
    }

    public int getSize() {
        return count.get();
    }

    public static void main(String[] args) {
    }
}
