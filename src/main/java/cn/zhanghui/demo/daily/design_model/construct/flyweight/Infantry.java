package cn.zhanghui.demo.daily.design_model.construct.flyweight;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Infantry
 * @description 享元模式的具体享元类
 * 这是一个步兵，这是可以重复建造的单位
 * @date 2020/6/23
 */
public class Infantry implements Soldier {
    // 兵种名称
    private String name;

    private String arms;

    private String flight;

    public Infantry(String name) {
        this.name = name;
    }

    @Override
    public void arm() {
        System.out.println("我的武器是：" + arms);
    }

    @Override
    public void fight() {
        System.out.println("我的作战方式是：" + flight);
    }
}
