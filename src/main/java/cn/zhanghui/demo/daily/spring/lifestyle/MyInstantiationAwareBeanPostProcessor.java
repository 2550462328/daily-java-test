package cn.zhanghui.demo.daily.spring.lifestyle;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;

import java.beans.PropertyDescriptor;

/**
 * @author ZhangHui
 * @version 1.0
 * @className MyInstantiationAwareBeanPostProcessor
 * @description 这是描述信息
 * @date 2020/9/7
 */
public class MyInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

    public MyInstantiationAwareBeanPostProcessor() {
        super();
        System.out.println("全局Bean实例化操作的InstantiationAwareBeanPostProcessor构造器方法");
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        System.out.println("[InstantiationAwareBeanPostProcessor]接口的postProcessBeforeInstantiation方法，在全局bean实例化前调用");
        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        System.out.println("[InstantiationAwareBeanPostProcessor]接口的postProcessAfterInstantiation方法，在全局bean实例化后调用");
        return true;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        System.out.println("[InstantiationAwareBeanPostProcessor]接口的postProcessPropertyValues方法，在全局bean设置某个属性时调用");
        return pvs;
    }
}
