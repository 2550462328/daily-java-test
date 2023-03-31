package cn.zhanghui.demo.daily.design_model.behavior.mediation;

/**
 * 中介者模式的抽象中介者
 *
 * @author ZhangHui
 * @date 2020/6/24
 */
public interface IRoom {

    void operate(String name, String message);

    void register(User user);

}
