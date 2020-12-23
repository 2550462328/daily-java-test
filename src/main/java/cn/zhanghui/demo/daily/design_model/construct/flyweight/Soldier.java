package cn.zhanghui.demo.daily.design_model.construct.flyweight;

/**
 * 享元模式的享元接口
 * 这里我们描述士兵，没错我们今天要生产士兵，各种各样的士兵，哈哈哈哈
 * @author ZhangHui
 * @date 2020/6/23
 */
public interface Soldier {
    /**
     * 装备武器
     */
    void arm();

    /**
     * 作战方式
     */
    void fight();
}
