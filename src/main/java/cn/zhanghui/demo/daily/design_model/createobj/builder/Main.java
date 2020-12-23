package cn.zhanghui.demo.daily.design_model.createobj.builder;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Main
 * @description 这是描述信息
 * @date 2020/6/22
 */
public class Main {
    public static void main(String[] args) {
        MealBuilder builder = new Suite1Builder();

        OrderSystem orderSystem = new OrderSystem(builder);

        Meal meal = orderSystem.build();

        System.out.println(" i have ordered the food " + meal.getFood() + " and  drink " + meal.getDrink());
    }
}
