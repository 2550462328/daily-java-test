package cn.zhanghui.demo.daily.design_model.construct.adapter.adapter_object;

public class Main {
    public static void main(String[] args) {
        PowerTarget target = new PowerAdapter();

        target.output5V();
    }
}
