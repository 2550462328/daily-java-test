package cn.zhanghui.demo.daily.design_model.construct.composite;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Department
 * @description 组合模式中的叶子节点
 * @date 2020/6/23
 */
public class SalesDepart implements Orginization {

    @Override
    public void add(Orginization orginization) {

    }

    @Override
    public void remove(Orginization orginization) {

    }

    @Override
    public void display() {

    }

    @Override
    public void run() {
        System.out.println("我是销售部，我为公司抛羊头洒热血~");
    }
}
