package cn.zhanghui.demo.daily.design_model.construct.composite;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Orginization
 * @description 组合模式组成部件
 * @date 2020/6/23
 */
public interface Orginization {

    void add(Orginization orginization);

    void remove(Orginization orginization);

    void display();

    // 每个组件自己的任务
    void run();
}
