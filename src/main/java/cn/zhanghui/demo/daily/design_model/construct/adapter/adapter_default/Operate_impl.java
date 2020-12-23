package cn.zhanghui.demo.daily.design_model.construct.adapter.adapter_default;

/**
 * 测试需要用到的类，这个类想要调用Operate接口中的某个功能
 * @ClassName: Operate_impl.java
 * @Description: 该类的功能描述
 * @author: ZhangHui
 * @date: 2019年12月2日 上午11:04:00
 */
public class Operate_impl {
	
	private Operate operate;
	
	public void implOperate(Operate operate) {
		this.operate = operate;
	}
	
	public void method2() {
		operate.method2();
	}
}
