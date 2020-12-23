package cn.zhanghui.demo.daily.base.thread.queue.delayQueue;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class WangMin implements Delayed {
    private String name;
    private String id;
    private long endTime;
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    
	public WangMin() {
		super();
	}
	public WangMin(String name, String id, long endTime) {
		super();
		this.name = name;
		this.id = id;
		this.endTime = endTime;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
    
	//相互比较排序用
	@Override
	public int compareTo(Delayed delayed) {
		WangMin w = (WangMin)delayed;
		return this.getDelay(this.timeUnit) - w.getDelay(this.timeUnit) >0 ?1:0;
	}
	// 用来判断是否到了下机时间
	@Override
	public long getDelay(TimeUnit unit) {
		//return unit.convert(endTime, TimeUnit.MILLISECONDS) - unit.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		return endTime - System.currentTimeMillis();
	}
}
