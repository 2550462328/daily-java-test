package cn.zhanghui.demo.daily.design_model.behavior.state;

/**
 * @author ZhangHui
 * @version 1.0
 * @className StateContext
 * @description 状态模式中的环境类
 * @date 2020/6/28
 */
public class StateContext {

    // 枚举出各个状态备用
    public static State slowState = new SlowState();

    public static State commonState = new CommonState();

    public static State frozenState = new FrozenState();

    private State state;

    public StateContext(State state){
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void request(){
        this.state.handle(this);
    }
}
