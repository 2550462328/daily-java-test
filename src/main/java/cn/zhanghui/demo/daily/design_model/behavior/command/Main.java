package cn.zhanghui.demo.daily.design_model.behavior.command;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Main
 * @description
 * @date 2020/6/24
 */
public class Main {
    public static void main(String[] args) {
        // 智能遥控器最多只能有20个孔位
        IntelligentControl iControl = new IntelligentControl(20);

        //给我们的智能遥控器安装功能
        iControl.setCommand(1,new AirCommand(new AirConditional()));
        iControl.setCommand(2,new TelCommand((new Television())));

        //现在打开它们
        iControl.onButton(1);
        iControl.onButton(2);

        // 现在关闭它们
        iControl.offButton(1);
        iControl.offButton(2);
    }
}
