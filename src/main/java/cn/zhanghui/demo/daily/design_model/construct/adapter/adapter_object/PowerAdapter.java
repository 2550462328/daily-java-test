package cn.zhanghui.demo.daily.design_model.construct.adapter.adapter_object;

/**
 * 
 * @ClassName: PowerAdapter.java
 * @Description: 这个是适配器模式中的适配器，用来实现将adaptee转换成target
 * @author: ZhangHui
 * @date: 2019年12月2日 上午10:17:53
 */
public class PowerAdapter implements PowerTarget  {
	
	// 这里引用的已有方法的实例对象，而不是通过继承的方法，这样的好处就是可以适配多个类
	private PowerAdaptee powerAdaptee = new PowerAdaptee();
	
	@Override
	public int output5V() {
		int output = powerAdaptee.output220V();
		
		output /= 44;
		System.out.println("经适配器转换后电压变成：" + output + "V");
		
		return output;
	}
}
