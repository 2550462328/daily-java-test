package cn.zhanghui.demo.daily.design_model.construct.bridge;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Main
 * @description 这是描述信息
 * @date 2020/6/23
 */
public class Main {
    public static void main(String[] args) {
        Student middleStudent = new MiddleStudent();

        middleStudent.setCouse(new PhysicalCourse());

        middleStudent.attendCourse();

        Student seniorStudent = new SeniorStudent();

        seniorStudent.setCouse(new MathCourse());

        seniorStudent.attendCourse();

    }
}
