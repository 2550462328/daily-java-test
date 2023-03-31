package cn.zhanghui.demo.daily.design_model.construct.adapter.adapter_object;

/**
 * @ClassName: PowerAdaptee.java
 * @Description: 这是适配器模式中的adaptee，已有的方法
 * @author: ZhangHui
 * @date: 2019年12月2日 上午10:14:42
 */
public class PowerAdaptee {
    private int output = 220;

    public int output220V() {
        System.out.println("已有的输出电压是 ：" + output + "V");
        return output;
    }
}
