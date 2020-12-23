package cn.zhanghui.demo.daily.design_model.behavior.state;

/**
 * @author ZhangHui
 * @version 1.0
 * @className State
 * @description 状态模式中的抽象状态类
 * @date 2020/6/28
 */
public interface State {
    void handle(StateContext context);
}
