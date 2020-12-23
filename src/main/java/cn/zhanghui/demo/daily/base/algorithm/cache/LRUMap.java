package cn.zhanghui.demo.daily.base.algorithm.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @ClassName: MyLinkedHashMap.java
 * @Description: 基于LinkedHashMap实现LRU缓存
 * @author: ZhangHui
 * @date: 2019年8月6日 下午2:51:49
 */
public class LRUMap<K, V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = 1L;

	private static int MAX_LIMIT = 100;

	private int limit;
	
	public LRUMap() {
		this(MAX_LIMIT);
	}
	public LRUMap(int cacheSize) {
		super(cacheSize,0.75f,true);
		this.limit = cacheSize;
	}
	
	public void save(K key, V value) {
		put(key, value);
	}
	
	public V getValue(K k) {
		return get(k);
	}
	
	public boolean exists(K k) {
		return containsKey(k);
	}
	
	/**
	 * 判断什么时候开始移除最近最少使用元素，也就是返回true
	 */
	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > limit;
	}

	@Override
	public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<K, V> entry : entrySet()) {
            sb.append(String.format("%s:%s ", entry.getKey(), entry.getValue()));
        }
        return sb.toString();
	}
	
	public static void main(String[] args) {
		LRUMap<String, String> map = new LRUMap<>(3);
		for(int i = 0; i < 10; i++) {
			map.save("key"+i, "value"+i);
		}
		System.out.println(map);
		map.save("keyx","valuex");
		System.out.println(map);
		map.get("key8");
		System.out.println(map);
		map.remove("keyx");
		System.out.println(map);
	}
	
}
