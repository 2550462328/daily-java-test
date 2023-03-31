package cn.zhanghui.demo.daily.design_model.createobj.factory.simple_factory;

/**
 * @ClassName: CarFactory.java
 * @Description: 车类工厂
 * @author: ZhangHui
 * @date: 2019年10月14日 上午9:19:52
 */
public class CarFactory implements VehileFactory {
    @Override
    public Moveable createCar() {
        return new Car();
    }
}
