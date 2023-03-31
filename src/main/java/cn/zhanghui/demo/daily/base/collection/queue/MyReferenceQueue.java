package cn.zhanghui.demo.daily.base.collection.queue;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 关于ReferenceQueue的测试
 *
 * @param null
 * @author ZhangHui
 * @date 2020/1/2
 * @return
 */
public class MyReferenceQueue {
    private static ReferenceQueue<byte[]> rq = new ReferenceQueue<>();
    private static int _1M = 1024 * 1024;

    public static void main(String[] args) {
        Map<WeakReference<byte[]>, Object> map = new HashMap<>();
        Object value = new Object();
        Thread thread = new Thread(MyReferenceQueue::run);
        thread.setDaemon(true);
        thread.start();

        // 放入数据
        for (int i = 0; i < 100; i++) {
            byte[] bytes = new byte[_1M];
            WeakReference<byte[]> weakReference = new WeakReference<>(bytes, rq);
            map.put(weakReference, value);
        }
        System.out.println("map.size = " + map.size());

        // 获取存活数据
        int aliveNum = 0;
        for (Entry<WeakReference<byte[]>, Object> entry : map.entrySet()) {
            if (entry != null) {
                if (entry.getKey().get() != null) {
                    aliveNum++;
                }
            }
        }
        System.out.println("100个对象存活的对象个数 = " + aliveNum);
    }

    // 监控线程
    public static void run() {
        int n = 0;
        WeakReference k;
        try {
            while ((k = (WeakReference) rq.remove()) != null) {
                System.out.println(++n + "回收了" + k);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
