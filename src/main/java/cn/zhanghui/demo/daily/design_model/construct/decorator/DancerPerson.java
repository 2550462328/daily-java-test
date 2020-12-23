package cn.zhanghui.demo.daily.design_model.construct.decorator;

/**
 * @author ZhangHui
 * @version 1.0
 * @className DancerPerson
 * @description 这里是装饰者模式中的具体装饰者
 *              这里给一个普通人赋予了跳舞的功能
 * @date 2020/6/23
 */
public class DancerPerson extends HasTalentPeople{

    public DancerPerson(Person person){
            super(person);
    }

    @Override
    public void eat() {
        // 这里对于一个会跳舞的人会怎么吃饭呢？
        System.out.println("吃饭之前我来跳个舞吧~");
        super.eat();
        System.out.println("吃完饭我再跳个舞吧！");
    }

    // 这里就是一个会跳舞的人比一个普通人多出来的技能了，当然还可以有其他的
    public void dance(){
        System.out.println("我还会唱歌呢》》》");
    }
}
