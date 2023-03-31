package cn.zhanghui.demo.daily.design_model.createobj.factory.spring_factory.myspring;

import cn.zhanghui.demo.daily.design_model.createobj.factory.spring_factory.Car;

public class Main {
    public static void main(String[] args) {
        try {
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
            Car car = (Car) context.getBean("car");
            car.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
