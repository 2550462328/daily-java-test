package cn.zhanghui.demo.daily.design_model.construct.facade;

/**
 * @author ZhangHui
 * @version 1.0
 * @className CPU
 * @description 外观模式中的子系统角色
 *              这里描述Memory的细节操作
 * @date 2020/6/23
 */
public class Memory implements Function {
    @Override
    public void start() {
        System.out.println("Memory正在启动");
    }

    @Override
    public void shutdown() {
        System.out.println("Memory正在关闭");
    }
}
