package cn.zhanghui.demo.daily.design_model.createobj.factory.simple_factory;

/**
 * @ClassName: Car.java
 * @Description: 汽车类
 * @author: ZhangHui
 * @date: 2019年10月14日 上午8:56:21
 */
public class Car implements Moveable {

    @Override
    public void run() {
        System.out.println("i have a car");
    }

}
