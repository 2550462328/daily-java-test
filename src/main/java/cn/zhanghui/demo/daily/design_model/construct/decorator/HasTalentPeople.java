package cn.zhanghui.demo.daily.design_model.construct.decorator;

/**
 * @author ZhangHui
 * @version 1.0
 * @className HasTalentPeople
 * @description 这里是装饰者模式的装饰者抽象类，注意它和具体被装饰对象实现同一接口
 *              这里已经实现了具体被装饰对象的基本功能，接下来需要给这个普通人添加什么功能就可以放飞自我啦
 * @date 2020/6/23
 */
public class HasTalentPeople implements Person {

    private Person person;

    public HasTalentPeople(Person person){
        this.person = person;
    }

    @Override
    public void eat() {
        person.eat();
    }

    // 可以添加一些其他的装饰类通用的方法
}
