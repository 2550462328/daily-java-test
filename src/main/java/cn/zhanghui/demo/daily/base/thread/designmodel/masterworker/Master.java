package cn.zhanghui.demo.daily.base.thread.designmodel.masterworker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Master {
    
	//1.一个盛放任务的容器
	private ConcurrentLinkedQueue<Task> workQueue = new ConcurrentLinkedQueue<Task>();
			
	//2.一个盛放Worker的集合
	private HashMap<String, Thread> workers = new HashMap<String, Thread>();
	
	//3.一个存放每一个Worker执行结果的集合
	private ConcurrentHashMap<String, Object> resultMap = new ConcurrentHashMap<String, Object>();
	
	//4.构造函数
	public Master(Worker worker, int workercount) {
		worker.setWorkQueue(this.workQueue);
		worker.setResultMap(this.resultMap);
		
		for(int i =0;i<workercount;i++) {
			this.workers.put(String.valueOf(i), new Thread(worker));
		}
	}
	//5.一个提交任务的方法
	public void submit(Task task) {
		this.workQueue.add(task);
	}
	
	//6.一个执行的方法
	public void execute() {
		for(Map.Entry<String, Thread> me :workers.entrySet()) {
			me.getValue().start();
		}
	}
	
	//7.判断是否结束
	public boolean isComplete() {
		for(Map.Entry<String, Thread> me :workers.entrySet()) {
			if(me.getValue().getState() != Thread.State.TERMINATED) {
				return false;
			}
		}
		return true;
	}
	
	//8.计算结果
	public int getResult() {
		int priceResult = 0;
		for(Map.Entry<String, Object> me : resultMap.entrySet()) {
			priceResult += (Integer)me.getValue();
		}
		return priceResult;
	}
}
