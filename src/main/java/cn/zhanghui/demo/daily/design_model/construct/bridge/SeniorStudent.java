package cn.zhanghui.demo.daily.design_model.construct.bridge;

/**
 * @author ZhangHui
 * @version 1.0
 * @className SeniorStudent
 * @description 桥接模式中的具体抽象化角色
 * @date 2020/6/23
 */
public class SeniorStudent extends Student {
    @Override
    void attendCourse() {
        System.out.println("我是一名高中生，我来上课啦~");
        this.course.study();
        System.out.println("下课了，真开心，上网吧啦~");
    }
}
