package cn.zhanghui.demo.daily.design_model.construct.proxy.proxy_jdk;

import cn.zhanghui.demo.daily.design_model.construct.proxy.proxy_static.BuyHouse;
import cn.zhanghui.demo.daily.design_model.construct.proxy.proxy_static.BuyHouseImpl;

import java.lang.reflect.Proxy;

/**
 * 
 * @ClassName: Main.java
 * @Description: 这里通过jdk动态代理的方式帮助BuyHouseImpl类增强代码，前提还是同一个接口
 *               原理是在jdk运行时根据BuyHouse接口生成一个新的实现类，通过反射进行增强
 * @author: ZhangHui
 * @date: 2019年12月2日 下午3:20:10
 */
public class Main {
	public static void main(String[] args) {
		// 这是自己买房的过程
		BuyHouse buyHouse = new BuyHouseImpl();
		buyHouse.buy();
		
		// 这是中介帮助买家买房的过程
		DynamicProxyHandler handler = new DynamicProxyHandler(buyHouse);
		
		BuyHouse buyHouseProxy = (BuyHouse) Proxy.newProxyInstance(BuyHouse.class.getClassLoader(), new Class[]{BuyHouse.class}, handler);
		buyHouseProxy.buy();
	}
}
