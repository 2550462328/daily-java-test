package cn.zhanghui.demo.daily.base.thread.threadlocal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 测试ThreadLocal内存溢出的情况
 * @author ZhangHui
 * @date 2020/1/2
 * @param null
 * @return
 */
public class ThreadLocalOOM {
	public static void main(String[] args) {
		ExecutorService executorService = Executors.newFixedThreadPool(500);
		ThreadLocal<List<User>> threalLocal = new ThreadLocal<>();
		try {
			for(int i =0; i < 100; i++) {
				executorService.execute(() ->{
					threalLocal.set(new ThreadLocalOOM().addBigList());
					System.out.println(Thread.currentThread().getName() + "has executed!");
					threalLocal.remove();
				});
			}
			Thread.sleep(1000L);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<User> addBigList(){
		List<User> list = new ArrayList<>(10000);
		for(int j = 0; j < 10000; j++) {
			list.add(new User(Thread.currentThread().getName(),"this is a test User" + j, 18884848, 25545454));
		}
		return list;
	}
}

class User {
	private String name;
	private String description;
	private int age;
	private int sex;

	public User(String name, String description, int age, int sex) {
		super();
		this.name = name;
		this.description = description;
		this.age = age;
		this.sex = sex;
	}
	
	
}