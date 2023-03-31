package cn.zhanghui.demo.daily.design_model.behavior.mediation;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Main
 * @description 这是描述信息
 * @date 2020/6/24
 */
public class Main {
    public static void main(String[] args) {
        User zhangsan = new User("zhangsan");

        User lisi = new User("lisi");

        IRoom iRoom = new ChatRoom();

        iRoom.register(zhangsan);
        iRoom.register(lisi);

        zhangsan.sendMessage("lisi", "你麻痹");
        lisi.sendMessage("zhangsan", "草泥马");
    }
}
