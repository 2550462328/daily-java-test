package cn.zhanghui.demo.daily.base.thread.base;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * @ClassName: ThreadExceptionTest.java
 * @Description: 处理多线程下的异常
 * @author: ZhangHui
 * @date: 2019年5月24日 下午5:11:05
 */
public class ThreadExceptionTest {
	public static void main(String[] args) throws Exception {
		//对ExceptionThread线程添加默认异常处理
		ExceptionThread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				System.out.println(t.getName() + "出现异常，异常信息：" + e.getMessage());
			}
		});
		Thread thread1 = new Thread(new ExceptionThread());
		thread1.setName("thread1");
		//对thread1线程异常进行处理
//		thread1.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
//			@Override
//			public void uncaughtException(Thread t, Throwable e) {
//				System.out.println(t.getName() + "出现异常，异常信息：" + e.getMessage());
//			}
//		});
		Thread thread2 = new Thread(new ExceptionThread());
		thread2.setName("thread2");
		//对thread2线程异常进行处理
//		thread2.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
//			@Override
//			public void uncaughtException(Thread t, Throwable e) {
//				System.out.println(t.getName() + "出现异常，异常信息：" + e.getMessage());
//			}
//		});
		thread1.start();
		thread2.start();
	}
}

// 制造异常的线程
class ExceptionThread extends Thread {
	@Override
	public void run() {
		System.out.println(1 / 0);
	}

}