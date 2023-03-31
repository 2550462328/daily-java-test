package cn.zhanghui.demo.daily.base.thread.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName: ReadWriteLockTest.java
 * @Description: 使用ReetrantLock控制容器的存取
 * @author: ZhangHui
 * @date: 2019年5月23日 下午4:38:15
 */
@Slf4j
public class BaseLockTest {
    private Lock lock = new ReentrantLock();

    private Object[] container = new Object[100];

    private int putstr, takestr, count;

    private Condition emptySign = lock.newCondition();
    private Condition fullSign = lock.newCondition();

    public void put(Object obj) {
        lock.lock();
        try {
            while (count == container.length) {
                fullSign.await();
            }
            container[putstr] = obj;
            if (++putstr == container.length) {
                putstr = 0;
            }
            count++;
            emptySign.signalAll();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    public Object take() {
        try {
            lock.lock();
            while (count == 0) {
                emptySign.await();
            }
            Object x = container[takestr];
            if (++takestr == container.length) {
                takestr = 0;
            }
            count--;
            fullSign.signalAll();
            return x;
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        BaseLockTest lockTest = new BaseLockTest();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int j = 0; j < 11; j++) {
                    lockTest.put("testobj");
                }
            }).start();
        }

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Arrays.toString(lockTest.container));
    }

}

