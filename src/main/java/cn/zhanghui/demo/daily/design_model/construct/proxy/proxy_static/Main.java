package cn.zhanghui.demo.daily.design_model.construct.proxy.proxy_static;

/**
 * @ClassName: Main.java
 * @Description: 这里通过静态代理的方法帮助BuyHouseImpl类增强代码
 * @author: ZhangHui
 * @date: 2019年12月2日 下午3:19:31
 */
public class Main {
    public static void main(String[] args) {
        // 这是自己买房的过程
        BuyHouse buyHouse = new BuyHouseImpl();
        buyHouse.buy();

        // 这是中介帮助买家买房的过程
        BuyHouseProxy buyHouseProxy = new BuyHouseProxy(buyHouse);
        buyHouseProxy.buy();
    }
}
