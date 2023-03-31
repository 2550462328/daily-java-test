package cn.zhanghui.demo.daily.design_model.createobj.factory.abstract_facrory;

public class AbstractFactory extends VehileFactory {

    @Override
    public Abstract_Car createCar() {
        return new MiniCar();
    }

    @Override
    public Abstract_Food createFood() {
        return new Banala();
    }

    @Override
    public Abstract_Play createPlay() {
        return new PlayPlane();
    }

}
