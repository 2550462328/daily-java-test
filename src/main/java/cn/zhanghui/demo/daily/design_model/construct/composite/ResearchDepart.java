package cn.zhanghui.demo.daily.design_model.construct.composite;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Department
 * @description 组合模式中的叶子节点
 * @date 2020/6/23
 */
public class ResearchDepart implements Orginization {

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
        System.out.println("我是研发部，我为公司科技发展贡献自己的力量~");
    }
}
