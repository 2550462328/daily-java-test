package cn.zhanghui.demo.daily.base.collection.map;

import java.util.TreeMap;
/**
 * 
 * @ClassName: MyTreeMap.java
 * @Description: 这是一个关于TreeMap的测试
 * @author: ZhangHui
 * @date: 2019年8月9日 下午3:23:17
 */
public class MyTreeMap {
	public static void main(String[] args) {
		TreeMap<A, String> map = new TreeMap<>();
		A a1 = new A(14);
		A a2 = new A(10);
		A a3 = new A(16);
		map.put(a1, "aa");
		map.put(a2, "bb");
		map.put(a3, "cc");
		System.out.println(map);
//		NavigableMap<A, String> desMap = map.descendingMap();
//		System.out.println(desMap);
//		desMap.put(new A(12), "cc");
//		desMap.pollFirstEntry();
//		SortedMap<A, String> sortedMap = map.subMap(a2, a3);
//		sortedMap.put(new A(12), "dd");
		System.out.println(map);
	}
}

class A implements Comparable<A>  {
	private int age;

	public A(int age) {
		this.age = age;
	}
	
	public A() {
		
	}
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public int compareTo(A a) {
		if(this.age == a.getAge())
			return 0;
		return this.age > a.getAge() ? 1 : -1;
	}

	@Override
	public String toString() {
		return "age：" + age;
	}
	
}