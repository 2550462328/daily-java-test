package cn.zhanghui.demo.daily.design_model.behavior.mediation;

/**
 * 中介者模式的抽象同事类
 *
 * @author ZhangHui
 * @date 2020/6/24
 */
public interface IUserService {

    void sendMessage(String name, String message);

    void receiveMessage(String message);

}
