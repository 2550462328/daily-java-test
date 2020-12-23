package cn.zhanghui.demo.daily.spring.lifestyle;

import org.springframework.beans.factory.*;

/**
 * @author ZhangHui
 * @version 1.0
 * @className User
 * @description 测试Bean的生命周期
 * @date 2020/9/4
 */
public class User implements BeanFactoryAware, BeanNameAware, InitializingBean, DisposableBean {

    private String name;
    private String userPwd;

    private BeanFactory beanFactory;
    private String beanName;

    public User(String name, String userPwd) {
        this.name = name;
        this.userPwd = userPwd;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        System.out.println("User调用setUserPwd方法设置userPwd属性");
        this.userPwd = userPwd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        System.out.println("User调用setName方法设置name属性");
        this.name = name;
    }

    public User(){
        System.out.println("User调用构造方法实例化");
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        System.out.println("[BeanFactoryAware]接口，调用setBeanFactory方法，获取BeanFactory的信息");
    }

    @Override
    public void setBeanName(String name) {
        System.out.println("[BeanNameAware]接口，调用setBeanName方法，获取beanName属性");
        this.beanName = name;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("[InitializingBean]接口，调用afterPropertiesSet方法，bean属性配置完成后进行一些操作");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("[DisposableBean]接口，调用destroy方法，bean销毁后进行一些操作");
    }

    public void myInit(){
        System.out.println("XML中定义的Bean在初始化时的自定义方法myInit");
    }

    public void myDestory(){
        System.out.println("XML中定义的Bean在销毁时的自定义方法myInit");
    }
}
