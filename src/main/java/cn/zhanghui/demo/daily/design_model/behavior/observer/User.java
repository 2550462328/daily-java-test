package cn.zhanghui.demo.daily.design_model.behavior.observer;

/**
 * @author ZhangHui
 * @version 1.0
 * @className User
 * @description 观察者模式中的具体观察者
 * @date 2020/6/28
 */
public class User implements IUser {

    private String name;

    private String message;

    public User(String name) {
        this.name = name;
    }

    @Override
    public void update(String message) {
        this.message = message;
        this.read();
    }

    public void read(){
        System.out.println(this.name + "接收到最新消息：" + message);
    }
}
