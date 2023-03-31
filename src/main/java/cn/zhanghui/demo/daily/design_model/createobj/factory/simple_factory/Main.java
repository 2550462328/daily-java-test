package cn.zhanghui.demo.daily.design_model.createobj.factory.simple_factory;

public class Main {
    public static void main(String[] args) {
        CarFactory carFactory = new CarFactory();
        Car myCar = (Car) carFactory.createCar();
        myCar.run();
    }
}
