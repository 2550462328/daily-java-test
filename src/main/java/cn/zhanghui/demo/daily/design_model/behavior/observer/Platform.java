package cn.zhanghui.demo.daily.design_model.behavior.observer;

/**
 * 观察者模式中的抽象主题，也就是抽象被观察者
 *
 * @author ZhangHui
 * @date 2020/6/28
 */
public interface Platform {
    void registerUser(IUser user);

    void removeUser(IUser user);

    void notifyUsers(String message);
}
