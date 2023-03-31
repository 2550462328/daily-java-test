package cn.zhanghui.demo.daily.design_model.construct.proxy.proxy_jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 通过反射调用object的method方法，参数是args
 *
 * @ClassName: BuyHouseProxy.java
 * @Description: 该类的功能描述
 * @author: ZhangHui
 * @date: 2019年12月2日 下午3:11:20
 */
public class DynamicProxyHandler implements InvocationHandler {

    private Object object;

    public DynamicProxyHandler(Object object) {
        this.object = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("寻找卖家...");
        Object result = method.invoke(object, args);
        System.out.println("完成买房！");
        return result;
    }
}
