package cn.zhanghui.demo.daily.design_model.behavior.mediation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZhangHui
 * @version 1.0
 * @className ChatRoom
 * @description 中介者模式的具体中介者
 * @date 2020/6/24
 */
public class ChatRoom implements IRoom {

    private Map<String, User> userMap = new HashMap<>();

    @Override
    public void operate(String name, String message) {
        if(userMap.containsKey(name)){
            ((User)(userMap.get(name))).receiveMessage(message);
        }
    }

    @Override
    public void register(User user) {
        userMap.put(user.getName(),user);
        user.setiRoom(this);
    }
}
