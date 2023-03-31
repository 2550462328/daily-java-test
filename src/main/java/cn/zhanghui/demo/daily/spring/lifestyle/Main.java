package cn.zhanghui.demo.daily.spring.lifestyle;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Main
 * @description 这是描述信息
 * @date 2020/9/7
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("接下来演示User这个Bean加载到Spring中的生命周期");

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-lifestyle.xml");

        System.out.println("Spring容器初始化成功，User该Bean已可正常使用");

        User user = applicationContext.getBean("user", User.class);

        System.out.println(user.getName() + "---" + user.getUserPwd());

        System.out.println("现在关闭Spring容器，销毁Bean");

        ((ClassPathXmlApplicationContext) applicationContext).registerShutdownHook();

//        接下来演示User这个Bean加载到Spring中的生命周期;
//
//        装配Bean之前BeanFactory处理器BeanFactoryPostProcessor的构造器方法;
//
//        [BeanFactoryPostProcessor]接口的postProcessBeanFactory方法，在装配Bean之前对BeanFactory信息的获取（此时Bean还未实例化）

//        全局Bean初始化操作的BeanPostProcessor构造器方法;
//
//        全局Bean实例化操作的InstantiationAwareBeanPostProcessor构造器方法;
//
//        [InstantiationAwareBeanPostProcessor]接口的postProcessBeforeInstantiation方法，在全局bean实例化前调用;
//
//        User调用构造方法实例化;
//
//        [InstantiationAwareBeanPostProcessor]接口的postProcessAfterInstantiation方法，在全局bean实例化后调用;
//
//        [InstantiationAwareBeanPostProcessor]接口的postProcessPropertyValues方法，在全局bean设置某个属性时调用;
//
//        User调用setName方法设置name属性;
//
//        User调用setUserPwd方法设置userPwd属性;

//        [BeanNameAware]接口，调用setBeanName方法，获取beanName属性;
//
//        [BeanFactoryAware]接口，调用setBeanFactory方法，获取BeanFactory的信息;
//
//        [BeanPostProcessor]接口的postProcessBeforeInitialization方法，在全局bean初始化前调用;
//
//        [InitializingBean]接口，调用afterPropertiesSet方法，bean属性配置完成后进行一些操作;
//
//        XML中定义的Bean在初始化时的自定义方法myInit;
//
//        [BeanPostProcessor]接口的postProcessAfterInitialization方法，在全局bean初始化后调用;
//
//        Spring容器初始化成功，User该Bean已可正常使用;
//
//        现在关闭Spring容器，销毁Bean;
//
//        [DisposableBean]接口，调用destroy方法，bean销毁后进行一些操作;
//
//        XML中定义的Bean在销毁时的自定义方法myInit;

    }
}
