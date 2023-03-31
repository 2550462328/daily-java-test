package cn.zhanghui.demo.daily.base.algorithm.limit;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * @ClassName TokenBuck
 * @Description: 使用令牌桶算法 + RateLimiter实现请求（写入）限流
 * 这里实现1分钟内只卖1000台手机的场景
 * @Author: ZhangHui
 * @Date: 2019/7/9
 * @Version：1.0
 */
public class TokenBuck {
    private AtomicInteger phoneNumbers = new AtomicInteger(0);
    private RateLimiter rateLimiter = RateLimiter.create(20d);  // 1秒内可以执行20次

    private final static int DEFAULT_LIMIT = 500;
    private int saleLimit;

    public TokenBuck(int saleLimit) {
        this.saleLimit = saleLimit;
    }

    public TokenBuck() {
        this(DEFAULT_LIMIT);
    }

    public int buy() {
        //这个check 必须放在success里面做判断，不然会产生线程安全问题(业务引起)
        //原因当phoneNumbers=99 时 同时存在三个线程进来。虽然phoneNumbers原子性，但是也会发生。如果必须写在这里，在success
        //里面也需要加上double check
        if (phoneNumbers.get() >= saleLimit) {
            throw new IllegalStateException("phone has been saled over!");
        }
        //目前设置超时时间，10秒内没有抢到就抛出异常
        //这里的TimeOut*Ratelimiter=总数  这里的超时就是让别人抢几秒，所以设置总数也可以由这里的超时和RateLimiter来计算
        boolean success = rateLimiter.tryAcquire(10, TimeUnit.SECONDS);
        if (success) {
            if (phoneNumbers.get() >= saleLimit) {
                throw new IllegalStateException("phone has been saled over!");
            }
            int phoneNo = phoneNumbers.getAndIncrement();
            System.out.println(Thread.currentThread().getName() + "has obtained phoneNo :" + phoneNo);
            return phoneNo;
        } else {
            // 超时后 同一时间，很大的流量来强时，超时快速失败
            throw new RuntimeException(Thread.currentThread().getName() + "has timeout and can try again...");
        }
    }

    public static void main(String[] args) {
        final TokenBuck tokenBuck = new TokenBuck(400);
        IntStream.range(0, 300).forEach(i -> {
            new Thread(tokenBuck::buy).start();
        });
    }
}
