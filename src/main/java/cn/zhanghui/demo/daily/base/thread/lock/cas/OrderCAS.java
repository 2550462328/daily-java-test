package cn.zhanghui.demo.daily.base.thread.lock.cas;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ZhangHui
 * @version 1.0
 * @className OrderCAS
 * @description 实现自旋锁的公平性
 * 就像银行排队办理业务一样，每个人（线程）进来首先领取自己的号，然后银行工作人员办理完一个业务后会依次递增去叫下一个人，从而试下有序办理业务
 * 缺点就是需要频繁对targetNum进行get和set操作，Atomic类底层用的是基于总线的CAS原理，所以会导致繁重的系统总线和内存的流量，导致性能下降
 * @date 2020/5/9
 */
public class OrderCAS {

    private AtomicInteger orderNum = new AtomicInteger();
    private AtomicInteger targetNum = new AtomicInteger();

    private ThreadLocal<Integer> targetNumHolder = new ThreadLocal<>();

    public void lock() {
        int currentOrderNum = orderNum.incrementAndGet();
        targetNumHolder.set(currentOrderNum);
        while (currentOrderNum != targetNum.get()) {
            // DO NOTHING
        }
    }

    public void unLock() {
        int paramsNum = targetNumHolder.get();
        targetNum.compareAndSet(paramsNum, paramsNum + 1);
    }
}
