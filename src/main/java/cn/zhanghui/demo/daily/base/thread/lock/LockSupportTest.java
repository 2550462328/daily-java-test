package cn.zhanghui.demo.daily.base.thread.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @ClassName LockSupportTest.class
 * @Description: 关于LockSupport的测试
 * @Author: ZhangHui
 * @Date: 2020/2/27
 * @Version：1.0
 */
public class LockSupportTest {
    public static void main(String[] args) throws InterruptedException {
        Thread threadA = new Thread(() -> {
            System.out.println("hello");
            // 一段时间获取不到许可证自动返回
            // LockSupport.parkNanos(new String("aa"),100000000);
            // LockSupport.park();
            new LockSupportTest().setBlock();
            System.out.println("world");
        });

        Thread threadB = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("你好");
            // 如果已经unpark过了 再调用park会直接返回
            LockSupport.unpark(threadA);
            System.out.println("世界");
        });

        threadA.start();
        // threadB.start();
    }

    private void setBlock() {
        // park里面的参数是用来记录线程被阻塞时被谁阻塞的,用于线程监控和分析工具来定位原因的。
        LockSupport.park(this);

    }
}
