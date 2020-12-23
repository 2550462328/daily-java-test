package cn.zhanghui.demo.daily.base.thread.queue.delayQueue;

import java.util.concurrent.DelayQueue;

public class WangBa implements 	Runnable {
	private DelayQueue<WangMin> queue = new DelayQueue<WangMin>();
	private boolean yinye = true;
	
	public void shangji(String name, String id, int money) {
		WangMin man = new WangMin(name,id,1000*money + System.currentTimeMillis());
		System.out.println("网民"+man.getName()+" 身份证"+man.getId()+" 交钱"+money+"块,开始上机...");
		this.queue.add(man);
	}
	
	public void xiaji(WangMin man) {
		System.out.println("网民"+man.getName()+" 身份证"+man.getId()+"已下机！");
	}
	@Override
	public void run() {
		while(yinye) {
			WangMin man;
			try {
				man = queue.take();
				xiaji(man);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			System.out.println("网吧开始营业...");
			WangBa siyu = new WangBa();
			Thread shangwang = new Thread(siyu);
			shangwang.start();
			
			siyu.shangji("张三","1", 1);
			siyu.shangji("李四","2", 5);
			siyu.shangji("王五","3", 10);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
