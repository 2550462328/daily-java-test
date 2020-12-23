package cn.zhanghui.demo.daily.design_model.createobj.builder;

import lombok.Getter;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Meal
 * @description 建造者模式中的Product
 *              这里以肯德基的午餐来举例，一份午餐包括food和drink
 * @date 2020/6/22
 */
@Getter
public class Meal {

    private String food;
    private String drink;

    public Meal setFood(String food){
        this.food = food;
        return this;
    }

    public Meal setDrink(String drink){
        this.drink = drink;
        return this;
    }

}
