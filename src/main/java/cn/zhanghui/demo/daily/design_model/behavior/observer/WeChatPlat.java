package cn.zhanghui.demo.daily.design_model.behavior.observer;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author ZhangHui
 * @version 1.0
 * @className WeChatPlat
 * @description 观察者模式中的具体主题，具体被观察者
 * @date 2020/6/28
 */
public class WeChatPlat implements Platform {

    private CopyOnWriteArraySet<IUser> userSet = new CopyOnWriteArraySet<>();

    @Override
    public synchronized void registerUser(IUser user) {
        userSet.add(user);
    }

    @Override
    public synchronized void removeUser(IUser user) {
        userSet.remove(user);
    }

    @Override
    public void notifyUsers(String message) {
        userSet.stream().forEach(user -> notifyUser(message,user));
    }

    private void notifyUser(String message, IUser user){
        user.update(message);
    }

    public void sendMessage(String message){
        notifyUsers(message);
    }
}
