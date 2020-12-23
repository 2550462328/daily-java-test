package cn.zhanghui.demo.daily.design_model.createobj.factory.abstract_facrory;
/**
 * @ClassName: VehileFactory.java
 * @Description: 抽象工厂的抽象层
 * @author: ZhangHui
 * @date: 2019年10月14日 上午9:38:16
 */
public abstract class VehileFactory {
	public abstract Abstract_Car createCar();
	public abstract Abstract_Food createFood();
	public abstract Abstract_Play createPlay();
}
