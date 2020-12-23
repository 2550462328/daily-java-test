package cn.zhanghui.demo.daily.design_model.construct.decorator;

/**
 * @author ZhangHui
 * @version 1.0
 * @className CommonPerson
 * @description 具体需要被装饰的类，装饰者模式中的ConcreteComponent
 *               这里是一个非常普通的普通人，接下来我们要给普通人装饰一些技能咯
 * @date 2020/6/23
 */
public class CommonPerson implements Person {

    @Override
    public void eat() {
        System.out.println("我是一个只会吃饭的普通人!");
    }

}
