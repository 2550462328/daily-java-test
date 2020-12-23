package cn.zhanghui.demo.daily.base.thread.threadlocal;

/**
 * InheritableThreadLocal的测试
 * @author ZhangHui
 * @date 2020/1/2
 * @param null
 * @return
 */
public  class InheritableThreadLocalTest{
	
	public static void main(String[] args){
		ThreadLocalInherit threadLocal = new ThreadLocalInherit();
		Task1 task1 = new Task1(threadLocal);
		Task2 task2 = new Task2(threadLocal);
		
		System.out.println(threadLocal.get());
		Thread thread1 = new Thread(task1);
		Thread thread2 = new Thread(task2);
		
		thread1.start();
		thread2.start();
	}
	
}
class Task1 implements Runnable {
	ThreadLocalInherit threadLocal;
	public Task1(ThreadLocalInherit threadLocal) { this.threadLocal = threadLocal; }
	@Override
	public void run() {
		if(threadLocal.get() == null) {
			System.out.println("当前为空");
			threadLocal.set("hello");
		}
		System.out.println(threadLocal.get());
	}
}

class Task2 implements Runnable {
	ThreadLocalInherit threadLocal;
	public Task2(ThreadLocalInherit threadLocal) { this.threadLocal = threadLocal; }
	@Override
	public void run() {
		if(threadLocal.get() == null) {
			System.out.println("当前为空");
			threadLocal.set("zhanghui");
		}
		System.out.println(threadLocal.get());
	}
}

class ThreadLocalImpl extends ThreadLocal<String> {
	@Override
	protected String initialValue() {
		return "init value";
	}
}

class ThreadLocalInherit extends InheritableThreadLocal<String>{
	@Override
	protected String childValue(String parentValue) {
		return parentValue + ", attachValue";
	}

	@Override
	protected String initialValue() {
		return "initValue";
	}
	
}