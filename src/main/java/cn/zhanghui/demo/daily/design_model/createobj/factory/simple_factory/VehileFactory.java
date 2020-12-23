package cn.zhanghui.demo.daily.design_model.createobj.factory.simple_factory;

/**
 * @ClassName: VehileFactory.java
 * @Description: 车类工厂的抽象层
 * @author: ZhangHui
 * @date: 2019年10月14日 上午9:18:15
 */
public interface VehileFactory {
	Moveable createCar();
}
