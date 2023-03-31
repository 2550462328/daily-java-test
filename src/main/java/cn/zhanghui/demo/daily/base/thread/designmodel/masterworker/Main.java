package cn.zhanghui.demo.daily.base.thread.designmodel.masterworker;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Master master = new Master(new Worker(), 20);

        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            Task t = new Task();
            t.setId(i);
            t.setPrice(r.nextInt(1000));
            master.submit(t);
        }
        master.execute();
        long start = System.currentTimeMillis();
        if (master.isComplete()) {
            long end = System.currentTimeMillis() - start;
            int priceResult = master.getResult();
            System.out.println("最终结果是:" + priceResult + ",执行时间:" + end);
        }
		/*while(true) {
			if(master.isComplete()) {
				long end = System.currentTimeMillis() - start;
				int priceResult = master.getResult();
				System.out.println("最终结果是:"+priceResult+",执行时间:"+end);
				break;
			}
		}*/
    }
} 
