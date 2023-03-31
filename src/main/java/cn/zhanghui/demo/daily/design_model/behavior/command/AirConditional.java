package cn.zhanghui.demo.daily.design_model.behavior.command;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Television
 * @description 命令模式里的接收者
 * 这里表示对空调的相关操作
 * @date 2020/6/24
 */
public class AirConditional {

    public void open() {
        System.out.println("打开空调");
    }

    public void off() {
        System.out.println("关闭空调");
    }
}
