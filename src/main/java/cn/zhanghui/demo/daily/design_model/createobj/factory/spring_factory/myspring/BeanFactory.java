package cn.zhanghui.demo.daily.design_model.createobj.factory.spring_factory.myspring;

/**
 * @ClassName: VehileFactory.java
 * @Description: Spring工厂类的抽象层
 * @author: ZhangHui
 * @date: 2019年10月14日 上午9:51:21
 */
public interface BeanFactory {
    Object getBean(String name);
}
