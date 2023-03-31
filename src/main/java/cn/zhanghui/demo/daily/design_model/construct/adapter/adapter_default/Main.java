package cn.zhanghui.demo.daily.design_model.construct.adapter.adapter_default;

public class Main {
    public static void main(String[] args) {
        Operate_impl operate_impl = new Operate_impl();

        // 可以看的出来如果这里直接new Operate的话会出现很多不必要的接口
        operate_impl.implOperate(new Operate() {
            @Override
            public void method2() {
            }

            @Override
            public void method1() {
            }
        });

        // 这里经过缺省适配器适配过后使用起来就很合理了
        operate_impl.implOperate(new DefaultAdapter() {
            @Override
            public void method2() {
                super.method2();
            }
        });
    }
}
