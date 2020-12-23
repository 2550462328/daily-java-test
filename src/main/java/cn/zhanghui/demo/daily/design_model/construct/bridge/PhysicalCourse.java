package cn.zhanghui.demo.daily.design_model.construct.bridge;

/**
 * @author ZhangHui
 * @version 1.0
 * @className MathCourse
 * @description 桥接模式中的具体实例化角色
 * @date 2020/6/23
 */
public class PhysicalCourse implements Course {
    @Override
    public void study() {
        System.out.println("now we are having physical course, open your book and turn to 13th page!");
    }
}
