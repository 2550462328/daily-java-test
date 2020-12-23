package cn.zhanghui.demo.daily.design_model.construct.proxy.proxy_cglib;

import cn.zhanghui.demo.daily.design_model.construct.proxy.proxy_static.BuyHouse;
import cn.zhanghui.demo.daily.design_model.construct.proxy.proxy_static.BuyHouseImpl;

/**
 * CGLIB创建的动态代理对象比JDK创建的动态代理对象的性能更高，但是CGLIB创建代理对象时所花费的时间却比JDK多得多。
 * 所以对于单例的对象，因为无需频繁创建对象，用CGLIB合适，反之使用JDK方式要更为合适一些。
 * 同时由于CGLib由于是采用动态创建子类的方法，对于final修饰的方法无法进行代理。
 * @ClassName: Main.java
 * @author: ZhangHui
 * @date: 2019年12月2日 下午3:20:10
 */
public class Main {
	public static void main(String[] args) {
		// 这是自己买房的过程
		BuyHouse buyHouse = new BuyHouseImpl();
		buyHouse.buy();
		
		// 这是中介帮助买家买房的过程
		CglibProxy cglibProxy = new CglibProxy();
		BuyHouseImpl buyHouseProxy = (BuyHouseImpl) cglibProxy.getInstance(buyHouse);
		buyHouseProxy.buy();
	}
}
