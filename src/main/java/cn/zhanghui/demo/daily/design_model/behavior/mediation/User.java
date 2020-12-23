package cn.zhanghui.demo.daily.design_model.behavior.mediation;

/**
 * @author ZhangHui
 * @version 1.0
 * @className User
 * @description 中介者模式的具体同事类
 * @date 2020/6/24
 */
public class User implements IUserService {

    private String name;

    private IRoom iRoom;

    public void setiRoom(IRoom iRoom) {
        this.iRoom = iRoom;
    }

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void sendMessage(String name, String message) {
        System.out.println(this.name + ":" + "hello, " + name + " , " + message);
        this.iRoom.operate(name, message);
    }

    @Override
    public void receiveMessage(String message) {
        System.out.println(this.name + " : 收到消息：" + message);
    }

}
