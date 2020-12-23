package cn.zhanghui.demo.daily.design_model.behavior.state;

/**
 * @author ZhangHui
 * @version 1.0
 * @className CommonState
 * @description 这是描述信息
 * @date 2020/6/28
 */
public class CommonState implements State {
    @Override
    public void handle(StateContext context) {
        System.out.println("回归正常模式！");
    }
}
