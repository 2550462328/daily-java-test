package cn.zhanghui.demo.daily.design_model.behavior.strategy;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Calculator
 * @description 策略模式中的环境类
 * @date 2020/6/28
 */
public class Calculator {

    private ComputeMethod computeMethod;

    public void setComputeMethod(ComputeMethod computeMethod) {
        this.computeMethod = computeMethod;
    }

    public int compute(int a, int b) {
        return this.computeMethod.compute(a, b);
    }
}
