package cn.zhanghui.demo.daily.my_jedis;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ZhangHui
 * @version 1.0
 * @className JedisPool
 * @description 这是描述信息
 * @date 2020/8/20
 */
public class JedisPool implements Pool<Jedis> {

    private String url;
    private int port;
    private int maxSize = 20;
    private long maxIdleTimeMills = 1000;

    // 空闲队列
    private LinkedBlockingQueue<Jedis> idleWorkQueue = null;
    // 活跃队列 可以用于统计活跃数
    private Queue<Jedis> activeWorkQueue = null;

    // 统计数量
    private AtomicInteger count = new AtomicInteger(0);

    public JedisPool(String url, int port) {
        this.url = url;
        this.port = port;
    }

    @Override
    public void init(int maxSize, long maxIdleTimeMills) {
        maxSize = maxSize;
        maxIdleTimeMills = maxIdleTimeMills;
        idleWorkQueue = new LinkedBlockingQueue<>(maxSize);
        activeWorkQueue = new LinkedBlockingQueue<>(maxSize);
    }

    @Override
    public Jedis getResource() throws Exception {
        Jedis jedis = null;

        // 1. 记录开始时间，检测超时
        long start = System.currentTimeMillis();

        while (true) {
            // 2. 从空闲队列中获取连接，如果拿到，一式两份存放到活动队列
            jedis = idleWorkQueue.poll();
            if (jedis != null) {
                activeWorkQueue.offer(jedis);
                return jedis;
            }

            // 3. 如果失败，判断池是否满，没满则创建
            if (count.get() < maxSize) {
                if (count.incrementAndGet() <= maxSize) {
                    jedis = new Jedis(url, port);
                    activeWorkQueue.offer(jedis);
                    System.out.printf("创建了新连接：%s \r\n", jedis.toString());
                    return jedis;
                } else {
                    count.decrementAndGet();
                }
            }
            
            // 4. 如果连接池满了，则在超时时间内进行等待
            try {
                jedis = idleWorkQueue.poll(maxIdleTimeMills - (System.currentTimeMillis() - start), TimeUnit.MILLISECONDS);
                if (jedis != null) {
                    activeWorkQueue.offer(jedis);
                    return jedis;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 4. 如果连接池满了，则在超时时间内进行等待
            if (maxIdleTimeMills < (System.currentTimeMillis() - start)) {
                throw new TimeoutException("Jedis: jedis connect timeout");
            }
        }
    }

    @Override
    public void release(Jedis jedis) {
        if (activeWorkQueue.remove(jedis)) {
            idleWorkQueue.add(jedis);
        }
    }
}
