package cn.zhanghui.demo.daily.design_model.construct.adapter.adapter_class;

/**
 * 
 * @ClassName: PowerAdapter.java
 * @Description: 这个是适配器模式中的适配器，用来实现将adaptee转换成target
 * @author: ZhangHui
 * @date: 2019年12月2日 上午10:17:53
 */
public class PowerAdapter extends PowerAdaptee implements PowerTarget  {
	//这里通过继承的方式去获得已有方法的能力，好处就是可以在这里面直接覆盖这个方法去做修改
	
	@Override
	public int output5V() {
		int output = super.output220V();
		
		output /= 44;
		System.out.println("经适配器转换后电压变成：" + output + "V");
		
		return output;
	}
}
