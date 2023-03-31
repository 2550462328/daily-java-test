package cn.zhanghui.demo.daily.design_model.createobj.builder;

/**
 * @author ZhangHui
 * @version 1.0
 * @className OrderSystem
 * @description 建造者模式中的Director
 * @date 2020/6/22
 */
public class OrderSystem {

    private MealBuilder builder;

    public OrderSystem(MealBuilder builder) {
        this.builder = builder;
    }

    public Meal build() {
        this.builder.buildFood();
        this.builder.buildMeal();
        return this.builder.getResult();
    }
}
