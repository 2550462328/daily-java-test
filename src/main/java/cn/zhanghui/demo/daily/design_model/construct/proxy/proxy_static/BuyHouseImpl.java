package cn.zhanghui.demo.daily.design_model.construct.proxy.proxy_static;

/**
 * 这个是买家
 * @ClassName: BuyHouseImpl.java
 * @Description: 该类的功能描述
 * @author: ZhangHui
 * @date: 2019年12月2日 下午3:11:30
 */
public class BuyHouseImpl implements BuyHouse {

	@Override
	public void buy() {
		System.out.println("我要买房...");
	}
}
