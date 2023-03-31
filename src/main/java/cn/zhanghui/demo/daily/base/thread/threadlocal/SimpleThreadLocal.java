package cn.zhanghui.demo.daily.base.thread.threadlocal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 这是一个简单的ThreadLocal
 *
 * @ClassName: SimpleThreadLocal.java
 * @Description: 该类的功能描述
 * @author: ZhangHui
 * @date: 2019年12月12日 下午2:54:55
 */
public class SimpleThreadLocal<T> {
    private Map<Thread, T> localMaps = Collections.synchronizedMap(new HashMap<>());

    protected T initValue() {
        return null;
    }

    public void set(T t) {
        localMaps.put(Thread.currentThread(), t);
    }

    public T get() {
        Thread thread = Thread.currentThread();
        T t = localMaps.get(thread);
        if (t == null && !localMaps.containsKey(thread)) {
            t = initValue();
            localMaps.put(thread, t);
        }
        return t;
    }

    public void clear() {
        localMaps.remove(Thread.currentThread());
    }
}
