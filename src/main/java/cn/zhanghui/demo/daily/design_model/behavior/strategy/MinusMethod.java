package cn.zhanghui.demo.daily.design_model.behavior.strategy;

/**
 * @author ZhangHui
 * @version 1.0
 * @className MinusMethod
 * @description 抽象模式中的具体策略类
 * @date 2020/6/28
 */
public class MinusMethod implements ComputeMethod {
    @Override
    public int compute(int a, int b) {
        return a - b;
    }
}
