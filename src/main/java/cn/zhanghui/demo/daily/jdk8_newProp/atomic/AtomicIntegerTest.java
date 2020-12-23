package cn.zhanghui.demo.daily.jdk8_newProp.atomic;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerTest {
	public static void main(String[] args) {
		AtomicInteger atomic = new AtomicInteger(2);
		System.out.println(atomic.updateAndGet(x -> Math.max(x,4))); // print 5
	}
}
