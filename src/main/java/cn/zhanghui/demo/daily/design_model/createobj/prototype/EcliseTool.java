package cn.zhanghui.demo.daily.design_model.createobj.prototype;

/**
 * @author ZhangHui
 * @version 1.0
 * @className EcliseTool
 * @description 原型模式中的ConcreProduct
 * @date 2020/6/22
 */
public class EcliseTool implements Tool {
    @Override
    public void use() {
        System.out.println("this is using Eclise Tool to resolve question");
    }

    @Override
    public Tool getClone() {
       EcliseTool tool = null;

        try {
            tool = (EcliseTool)clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return tool;
    }
}
