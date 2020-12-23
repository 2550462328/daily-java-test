package cn.zhanghui.demo.daily.design_model.behavior.state;

/**
 * @author ZhangHui
 * @version 1.0
 * @className SlowState
 * @description 状态模式中的具体状态类
 * @date 2020/6/28
 */
public class SlowState implements State {

    @Override
    public void handle(StateContext context) {
        System.out.println("进入减速模式");
        context.setState(StateContext.commonState);
    }
}
