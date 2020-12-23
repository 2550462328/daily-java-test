package cn.zhanghui.demo.daily.base.thread.threadpool;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Schedule类线程池框架的使用
 * @author ZhangHui
 * @date 2020/1/2
 * @param null
 * @return
 */
class Task extends Thread {
	@Override
	public void run() {
		System.out.println(Calendar.getInstance().getTime().getSeconds() + "run");
	}
}
public class ScheduledJob {
	public static void main(String[] args) {
		Task task = new Task();
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
		ScheduledFuture<?>  schedulerTask = scheduler.scheduleWithFixedDelay(task, 1, 3, TimeUnit.SECONDS);
	}
}
