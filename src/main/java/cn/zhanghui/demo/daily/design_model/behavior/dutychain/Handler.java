package cn.zhanghui.demo.daily.design_model.behavior.dutychain;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Handler
 * @description 这是描述信息
 * @date 2020/6/24
 */
public abstract class Handler {

    protected abstract void handleRequest(int price);

    private   Handler successor = null;

    public void setSuccessor(Handler successor) {
        this.successor = successor;
    }

    public Handler getSuccessor() {
        return successor;
    }
}