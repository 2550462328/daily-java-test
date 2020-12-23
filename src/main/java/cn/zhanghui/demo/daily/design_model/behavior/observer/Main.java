package cn.zhanghui.demo.daily.design_model.behavior.observer;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Main
 * @description 这是描述信息
 * @date 2020/6/28
 */
public class Main {
    public static void main(String[] args) {
        IUser zhangsan = new User("zhangsan");
        IUser lisi = new User("lisi");

        Platform wechatPlat = new WeChatPlat();
        wechatPlat.registerUser(zhangsan);
        wechatPlat.registerUser(lisi);

        ((WeChatPlat) wechatPlat).sendMessage("欢迎关注公众号：小白说，这里给您新的用户体验！");
    }
}
