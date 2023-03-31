package cn.zhanghui.demo.daily.design_model.createobj.prototype;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Tool
 * @description 原型模式的Product
 * @date 2020/6/22
 */
public interface Tool extends Cloneable {

    void use();

    Tool getClone();
}
