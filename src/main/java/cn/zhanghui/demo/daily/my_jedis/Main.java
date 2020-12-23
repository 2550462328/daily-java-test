package cn.zhanghui.demo.daily.my_jedis;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Main
 * @description 这是描述信息
 * @date 2020/8/20
 */
public class Main {

    public static void main(String[] args) {
//        Jedis jedis = new Jedis("localhost",6379);
//        try {
//            jedis.set("zhanghui","zuizhuai");
//
//            jedis.set("zhang","hui","NX","EX",100000);
//
//            System.out.println(jedis.get("zhanghui"));
//
//            TimeUnit.SECONDS.sleep(10);
//
//            System.out.println(jedis.get("zhang"));
//
//            jedis.append("zhanghui","-zhendehaoshuai");
//
//            System.out.println(jedis.get("zhanghui"));
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            jedis.close();
//        }

        CountDownLatch downLatch = new CountDownLatch(1);

        AtomicInteger count = new AtomicInteger(0);

        JedisPool jedisPool = new JedisPool("localhost", 6379);

        jedisPool.init(20, 2000);

        for (int i = 0; i < 21; i++) {
            new Thread(() -> {
                try {
                    count.incrementAndGet();
                    downLatch.await();

                    Jedis jedis = jedisPool.getResource();

                    TimeUnit.SECONDS.sleep(1);

                    jedisPool.release(jedis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }).start();
        }

        while (count.get() != 21) {
            //DO NOTHING
        }
        downLatch.countDown();
    }


}
