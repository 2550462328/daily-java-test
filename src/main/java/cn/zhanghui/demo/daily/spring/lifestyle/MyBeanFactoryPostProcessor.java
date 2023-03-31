package cn.zhanghui.demo.daily.spring.lifestyle;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * @author ZhangHui
 * @version 1.0
 * @className MyBeanFactoryPostProcessor
 * @description 这是描述信息
 * @date 2020/9/7
 */
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    public MyBeanFactoryPostProcessor() {
        super();
        System.out.println("装配Bean之前BeanFactory处理器BeanFactoryPostProcessor的构造器方法");
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("[BeanFactoryPostProcessor]接口的postProcessBeanFactory方法，在装配Bean之前对BeanFactory信息的获取（此时Bean还未实例化）");
    }
}
