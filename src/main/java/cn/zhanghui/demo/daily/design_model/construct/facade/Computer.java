package cn.zhanghui.demo.daily.design_model.construct.facade;


/**
 * @author ZhangHui
 * @version 1.0
 * @className Computer
 * @description 外观模式中的门面角色
 * @date 2020/6/23
 */
public class Computer implements Function {
    private CPU cpu;
    private Disk disk;
    private Memory memory;

    public Computer() {
        cpu = new CPU();
        disk = new Disk();
        memory = new Memory();
    }

    @Override
    public void start() {
        disk.start();
        memory.start();
        cpu.start();
    }

    @Override
    public void shutdown() {
        disk.shutdown();
        memory.shutdown();
        cpu.shutdown();
    }
}
