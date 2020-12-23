package cn.zhanghui.demo.daily.design_model.construct.flyweight;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Main
 * @description 这是描述信息
 * @date 2020/6/23
 */
public class Main {
    public static void main(String[] args) throws ClassNotFoundException {
        SoldierFactory factory = new SoldierFactory();

        for(int i =0; i  < 10; i++){
            factory.produceUnit("infantry");
            factory.produceUnit("archer");
        }
    }
}
