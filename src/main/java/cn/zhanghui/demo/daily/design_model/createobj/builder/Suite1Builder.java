package cn.zhanghui.demo.daily.design_model.createobj.builder;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Suite1Builder
 * @description 建造者模式中的ConcreteBuilder
 * @date 2020/6/22
 */
public class Suite1Builder extends MealBuilder {

    @Override
    public Meal buildFood() {
        return super.meal.setFood("牛肉汉堡");
    }

    @Override
    public Meal buildMeal() {
        return  super.meal.setDrink("大杯可乐");
    }
}
