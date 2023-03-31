package cn.zhanghui.demo.daily.spring.lifestyle;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author ZhangHui
 * @version 1.0
 * @className MyBeanPostProcessor
 * @description 这是描述信息
 * @date 2020/9/7
 */
public class MyBeanPostProcessor implements BeanPostProcessor {

    public MyBeanPostProcessor() {
        super();
        System.out.println("全局Bean初始化操作的BeanPostProcessor构造器方法");
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("[BeanPostProcessor]接口的postProcessBeforeInitialization方法，在全局bean初始化前调用");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("[BeanPostProcessor]接口的postProcessAfterInitialization方法，在全局bean初始化后调用");
        return bean;
    }
}
