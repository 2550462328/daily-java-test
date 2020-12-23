package cn.zhanghui.demo.daily.design_model.behavior.strategy;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Main
 * @description 这是描述信息
 * @date 2020/6/28
 */
public class Main {
    public static void main(String[] args) {
        Calculator calculator = new Calculator();

        calculator.setComputeMethod(new AddMethod());

        System.out.println(calculator.compute(1,2));

        calculator.setComputeMethod(new MinusMethod());

        System.out.println(calculator.compute(2,1));
    }
}
