package cn.zhanghui.demo.daily.design_model.behavior.command;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Television
 * @description 命令模式里的接收者
 * 这里表示对电视的相关操作
 * @date 2020/6/24
 */
public class Television {

    public void open() {
        System.out.println("打开电视");
    }

    public void off() {
        System.out.println("关闭电视");
    }
}
