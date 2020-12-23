package cn.zhanghui.demo.daily.design_model.construct.facade;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Main
 * @description 这是描述信息
 * @date 2020/6/23
 */
public class Main {
    public static void main(String[] args) {
        // 这里在使用的时候只需要关注Computer就好，封装了具体的系统细节
        Computer computer = new Computer();

        computer.start();
        System.out.println("欢迎来到英雄联盟！");
        computer.shutdown();
    }
}
