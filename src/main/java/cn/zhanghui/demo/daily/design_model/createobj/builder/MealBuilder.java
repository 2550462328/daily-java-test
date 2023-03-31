package cn.zhanghui.demo.daily.design_model.createobj.builder;

/**
 * @author ZhangHui
 * @version 1.0
 * @className MealBuilder
 * @description 建造者模式中的Builder
 * @date 2020/6/22
 */
public abstract class MealBuilder {

    public Meal meal = new Meal();

    public abstract Meal buildFood();

    public abstract Meal buildMeal();

    public Meal getResult() {
        return this.meal;
    }

}
