package cn.zhanghui.demo.daily.base.thread.designmodel.futuremodel;

public class Main {
   
    public static void main(String[] args) {
    	 FutureClient fc = new FutureClient();
    	 Data data = fc.request("查询参数");
    	 System.out.println("请求发送成功");
    	 System.out.println("做点其他的事");
    	 
    	 String result = data.getRequest();
    	 System.out.println("获取到结果"+result);
	}
}
