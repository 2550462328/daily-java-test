package cn.zhanghui.demo.daily.base.util;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Java的Timer实现定时任务 比较scheduled和scheduledFixRate
 * 
 * @ClassName: TimerTest.java
 * @Description: 该类的功能描述
 * @author: ZhangHui
 * @date: 2019年5月24日 上午9:56:55
 */
public class TimerTest {
	public static int i = 0;

	public static void main(String[] args) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, -10);
		Timer timer = new Timer();
		MyTask task = new MyTask();
		System.out.println("task计划执行 : " + new DateTime(calendar.getTimeInMillis()).toString("yyyy-MM-dd HH:mm:ss"));
		// 在指定时间后每隔1000ms执行一次
		timer.schedule(task, calendar.getTime(), 1000);
		// 在指定时间后每隔1000ms执行一次,具有追赶性，在计划开始时间小于当前时间时，系统会判断是否执行间隔的任务
		// timer.scheduleAtFixedRate(task, calendar.getTime(), 1000);
		// 1000ms后每1000ms执行一次
		// timer.schedule(task, 1000, 1000);
		// 取消timer中所有的task
		// timer.cancel();
	}
}

class MyTask extends TimerTask {
	@Override
	public void run() {
		TimerTest.i++;
		System.out.println(TimerTest.i);
	}
}