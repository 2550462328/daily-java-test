package cn.zhanghui.demo.daily.design_model.createobj.factory.spring_factory.mockspring;

import java.util.Properties;

import cn.zhanghui.demo.daily.design_model.createobj.factory.spring_factory.Car;
import junit.framework.Test;

/**
 * @ClassName: Main.java
 * @Description: 模拟spring
 * @author: ZhangHui
 * @date: 2019年10月14日 上午10:02:22
 */
public class Main {
	public static void main(String[] args) throws Exception {
		Properties springProp = new Properties();
		springProp.load(Test.class.getClassLoader().getResourceAsStream("spring.properties"));
		String carPath = springProp.getProperty("carClass");
		Class<Car> carClass = (Class<Car>) Class.forName(carPath);
		Car newCar = carClass.newInstance();
		newCar.run();
	}
}
