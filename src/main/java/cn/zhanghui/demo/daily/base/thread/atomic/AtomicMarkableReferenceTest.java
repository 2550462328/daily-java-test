package cn.zhanghui.demo.daily.base.thread.atomic;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * @author ZhangHui
 * @version 1.0
 * @className AtomicMarkableReferenceTest
 * @description 测试AtomicMarkableReference能否解决CAS的ABA问题
 *              结论AtomicMarkableReference不能规避ABA的问题，只能降低ABA的几率
 *              它的原理是更改版本号在true和false之间变动，有可能会出现true --> false --> true的情况，而在另一个线程看来value没有变化，版本号也没有变化，那我是可以进行更改的
 * @dae 2020/5/12
 */
public class AtomicMarkableReferenceTest {

    private  static AtomicMarkableReference<Integer> atomicMarkableReference = new AtomicMarkableReference<>(100,true);

    public static void main(String[] args) {

        //t1将atomicMarkableReference的值从100变成101，然后又变成100.模拟出ABA的问题
        Thread t1 = new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(1L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            atomicMarkableReference.compareAndSet(100,101,atomicMarkableReference.isMarked(),!atomicMarkableReference.isMarked());
            atomicMarkableReference.compareAndSet(101,100,atomicMarkableReference.isMarked(),!atomicMarkableReference.isMarked());

        });

        Thread t2 = new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(2L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //t2在t1模拟出ABA问题后再去尝试更改atomicMarkableReference的值
            boolean isSuccess = atomicMarkableReference.compareAndSet(100,101,atomicMarkableReference.isMarked(),!atomicMarkableReference.isMarked());

            // 预期是false，应该这个100不是最开始的那个100
            // 实际返回true，说明AtomicMarkableReference根本不能规避ABA的问题
            System.out.println(isSuccess);
        });

        t1.start();
        t2.start();
    }
}
