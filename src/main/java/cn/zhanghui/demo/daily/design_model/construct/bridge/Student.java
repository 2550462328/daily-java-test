package cn.zhanghui.demo.daily.design_model.construct.bridge;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Student
 * @description 桥接模式中的抽象化角色
 * @date 2020/6/23
 */
public abstract class Student {

    protected Course course;

    public void setCouse(Course course) {
        this.course = course;
    }

    abstract void attendCourse();
}
