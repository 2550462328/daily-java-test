package cn.zhanghui.demo.daily.base.collection.map;

import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @ClassName: MyWeakHashMap.java
 * @Description: 这是一个关于WeakHashMap的测试
 * @author: ZhangHui
 * @date: 2019年8月9日 下午3:23:36
 */
public class MyWeakHashMap<K, V> {

	private int size;

	private ConcurrentHashMap<K, V> useMap;

	private WeakHashMap<K, V> cacheMap;

	public MyWeakHashMap(int size, ConcurrentHashMap<K, V> useMap, WeakHashMap<K, V> cacheMap) {
		super();
		this.size = size;
		this.useMap = useMap;
		this.cacheMap = cacheMap;
	}

	public void put(K k, V v) {
		if (useMap.size() >= size) {
			synchronized (cacheMap) {
				cacheMap.putAll(useMap);
				cacheMap.clear();
			}
		}
		useMap.put(k, v);
	}

	public V get(K k) {
		V v = useMap.get(k);
		if (v == null) {
			synchronized (cacheMap) {
				v = cacheMap.get(k);
			}
			if (v != null) {
				useMap.put(k, cacheMap.get(k));
			}
		}
		return v;
	}

	public static void main(String[] args) {
		WeakHashMap<Object, String> map = new WeakHashMap<>();
		
//		for (int i = 0; i < 20; i++) {
//			map.put("i" + i, "i" + i);
//		}
//		System.out.println(map.size());
//		System.gc();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(map.size());
//		===================================
		
		Integer s1 = 118;
		String s2 = "s2";
		map.put(s1, "s1");
		map.put(s2, "s2");
		System.out.println("gc前，map=" + map);
		s1 = null;
		System.out.println(s1);
		System.gc();
		System.out.println("gc后，map=" + map);
	}
}
