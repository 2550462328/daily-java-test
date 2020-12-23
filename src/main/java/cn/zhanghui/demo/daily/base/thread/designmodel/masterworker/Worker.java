package cn.zhanghui.demo.daily.base.thread.designmodel.masterworker;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Worker implements Runnable {
	    private ConcurrentLinkedQueue<Task> workQueue;
	    private ConcurrentHashMap<String, Object> resultMap;
	    
	    public void setWorkQueue(ConcurrentLinkedQueue<Task> workQueue) {
			this.workQueue = workQueue;
		}

		public void setResultMap(ConcurrentHashMap<String, Object> resultMap) {
			this.resultMap = resultMap;
		}

	@Override
	public void run() {
        while(true) {
        	Task input = this.workQueue.poll();
        	if(input == null) break;
        	Object object = handle(input);
        	this.resultMap.put(String.valueOf(input.getId()), object);
        }
	}
	
	public Object handle(Task input) {
		Object output = null;
		try {
			Thread.sleep(500);
			output = input.getPrice();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return output;
	}
}
