package cn.zhanghui.demo.daily.design_model.construct.proxy.proxy_static;

/**
 * 这个是中介
 * @ClassName: BuyHouseProxy.java
 * @Description: 该类的功能描述
 * @author: ZhangHui
 * @date: 2019年12月2日 下午3:11:20
 */
public class BuyHouseProxy implements BuyHouse {
	
	private BuyHouse buyHouse;
	
	public BuyHouseProxy(BuyHouse buyHouse) {
		super();
		this.buyHouse = buyHouse;
	}

	@Override
	public void buy() {
		System.out.println("寻找卖家...");
		this.buyHouse.buy();
		System.out.println("完成买房！");
	}
}
