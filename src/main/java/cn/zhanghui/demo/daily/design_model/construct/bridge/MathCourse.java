package cn.zhanghui.demo.daily.design_model.construct.bridge;

/**
 * @author ZhangHui
 * @version 1.0
 * @className MathCourse
 * @description 桥接模式中的具体实例化角色
 * @date 2020/6/23
 */
public class MathCourse implements Course {
    @Override
    public void study() {
        System.out.println("now we are having math course, stand up please!");
    }
}
