package cn.zhanghui.demo.daily.base.thread.atomic;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * @author ZhangHui
 * @version 1.0
 * @className AtomicStampReferenceTest
 * @description 基于AtomicStampReference查看能够规避CAS中ABA的问题
 * 核心就是线程在使用AtomicStampReference之前要先拿到stamp（版本号），在compareAndSet之前进行比较
 * @date 2020/5/12
 */
public class AtomicStampReferenceTest {

    private static AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference(100, 0);

    public static void main(String[] args) {

        //t1将atomicStampedReference的值从100变成101，然后又变成100.模拟出ABA的问题
        Thread t1 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            atomicStampedReference.compareAndSet(100, 101, atomicStampedReference.getStamp(), atomicStampedReference.getStamp() + 1);
            atomicStampedReference.compareAndSet(101, 100, atomicStampedReference.getStamp(), atomicStampedReference.getStamp() + 1);

        });

        Thread t2 = new Thread(() -> {
            int stamp = atomicStampedReference.getStamp();
            try {
                TimeUnit.SECONDS.sleep(2L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //t2在t1模拟出ABA问题后再去尝试更改atomicStampedReference的值
            // 当然因为版本号不对，不可能修改成功
            boolean isSuccess = atomicStampedReference.compareAndSet(100, 101, stamp, stamp + 1);

            System.out.println(isSuccess);
        });

        t1.start();
        t2.start();
    }
}
