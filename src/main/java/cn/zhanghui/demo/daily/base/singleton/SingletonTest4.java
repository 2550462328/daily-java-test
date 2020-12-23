package cn.zhanghui.demo.daily.base.singleton;

/**
 * 
 * @ClassName: SingletonTest3.java
 * @Description: 使用枚举类实现单例
 * @author: ZhangHui
 * @date: 2019年5月24日 上午11:33:58
 */
public class SingletonTest4 {
	public static void main(String[] args) {
		Thread thread1 = new Thread(new ThreadD());
		Thread thread2 = new Thread(new ThreadD());
		Thread thread3 = new Thread(new ThreadD());
		thread1.start();
		thread2.start();
		thread3.start();
	}
}
//将枚举类封装在Object里面
class UniqueObject4 {
	public enum ObjectEnum {
		object;
		private UniqueObject4 uniqueObject4 = new UniqueObject4();
		public UniqueObject4 getObject() {
			return uniqueObject4;
		}
	}
	public static UniqueObject4 getInstance() {
		return ObjectEnum.object.getObject();
	}
}

 class ThreadD implements Runnable {
	@Override
	public void run() {
		UniqueObject4 object = UniqueObject4.getInstance();
		System.out.println(object.hashCode());
	}
}