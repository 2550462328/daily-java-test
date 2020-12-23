package cn.zhanghui.demo.daily.design_model.behavior.state;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Main
 * @description 这是描述信息
 * @date 2020/6/28
 */
public class Main {
    public static void main(String[] args) {
        StateContext context = new StateContext(StateContext.commonState);

        context.setState(StateContext.slowState);

        context.request();

        context.request();
    }
}
