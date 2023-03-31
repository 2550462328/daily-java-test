package cn.zhanghui.demo.daily.design_model.construct.proxy.proxy_cglib;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 这里通过cglib字节码工具，在运行时生成target类的子类，然后拦截调用父类的方法，进行增强
 * 需要注意的是因为代理类是继承target类，所以如果target类是final的就不可继承
 *
 * @ClassName: BuyHouseProxy.java
 * @Description: 该类的功能描述
 * @author: ZhangHui
 * @date: 2019年12月2日 下午3:11:20
 */
public class CglibProxy implements MethodInterceptor {

    private Object target;

    public Object getInstance(final Object target) {
        this.target = target;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.target.getClass());
        enhancer.setCallback(this);

        return enhancer.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("寻找卖家...");
        Object result = proxy.invoke(obj, args);
        System.out.println("完成买房！");
        return result;
    }

}
