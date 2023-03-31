package cn.zhanghui.demo.daily.base.reference;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName: MySoftReference_2.java
 * @Description: 软引用的引用对象可能在一段时间后会回收器回收 但是软引用对象（SoftReference）本身也是个对象，也可能会导致内存移除
 * @author: ZhangHui
 * @date: 2019年8月27日 下午3:30:59
 */
public class MySoftReference_2 {
    public static class MyBigObject {
        int[] data = new int[128];
    }

    public static int CACHE_INIT_CAPABLITY = 100_000;
    // 静态集合保存软引用，会导致这些软引用对象本身无法被垃圾回收器回收
    public static Set<SoftReference<MyBigObject>> cache = new HashSet<>(CACHE_INIT_CAPABLITY);

    // 存储被清理的软应用对象
    public static ReferenceQueue<MyBigObject> rq = new ReferenceQueue<>();

    // 清理次数
    public static int clearRefNum = 0;

    public static void main(String[] args) {
        for (int i = 0; i < 100_000; i++) {
            MyBigObject object = new MyBigObject();
            if (i % 10_000 == 0)
                System.out.println("cache.size = " + cache.size());
            cache.add(new SoftReference<>(object, rq));
            // 手动清除SoftReference
            clearUnlessRef();
        }
        System.out.println("END , clearRefNum = " + clearRefNum);
    }

    public static void clearUnlessRef() {
        Reference<? extends MyBigObject> ref;
        while ((ref = rq.poll()) != null) {
            if (cache.remove(ref)) {
                clearRefNum++;
            }
        }
    }
}
