package cn.zhanghui.demo.daily.base.thread.designmodel.futuremodel;

public class RealData implements Data {
    
	private String result;
	@Override
	public String getRequest() {
		return result;
	}
	public RealData(String queryStr) {
		System.out.println("根据"+queryStr+"进行查询，这是一个很耗时的操作...");
		try {
			//模拟操作时长
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("操作完毕，获取结果");
		result = "查询结果";
	}
}
