package cn.zhanghui.demo.daily.base.collection.map;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 
 * @ClassName: ConcurrentHashMapTest.java
 * @Description: ConcurrentHashMap的相关测试
 * @author: ZhangHui
 * @date: 2019年11月28日 上午11:41:15
 */
public class ConcurrentHashMapTest {
	
	public static int a = 2;

	public static void main(String[] args) {
		ConcurrentMap map1 = new ConcurrentHashMap<>();

		ConcurrentMap map2 = new ConcurrentSkipListMap();

		CopyOnWriteArrayList copyOnWriteArrayList = new CopyOnWriteArrayList();

		CopyOnWriteArraySet copyOnWriteArraySet = new CopyOnWriteArraySet();

		ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue();

		BlockingQueue queue1 = new ArrayBlockingQueue(1);

		BlockingQueue queue2 = new LinkedBlockingQueue();

		BlockingQueue queue3 = new PriorityBlockingQueue();

		BlockingQueue queue4 = new SynchronousQueue();

		BlockingQueue queue5 = new DelayQueue();


		//在 computeIfAbsent中调用computeIfAbsent会发生栈溢出，jdk1.9中已修复
//		map.computeIfAbsent("bb",  key -> map.computeIfAbsent("aa", key2 -> "value"));
		map1.put("aaa", "sss");
		System.out.println("it is ok!");
		a += 3;
		
		String s = null;
		System.out.println(s.length());
	}
}
