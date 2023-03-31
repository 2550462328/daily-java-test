package cn.zhanghui.demo.daily.base.thread.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @ClassName LockSupportBlockerTest.class
 * @Description: 测试LockSupport中blocker的取值
 * @Author: ZhangHui
 * @Date: 2020/2/27
 * @Version：1.0
 */
public class LockSupportBlockerTest {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = Thread.currentThread();

        Thread thread1 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {

            }
            System.out.println(thread.getName() + "before block : block info is " + LockSupport.getBlocker(thread));
            LockSupport.unpark(thread);
        });
        thread1.start();
        LockSupport.park(new String("blocker"));

        System.out.println(thread.getName() + "after block : block info is " + LockSupport.getBlocker(thread));
    }
}
