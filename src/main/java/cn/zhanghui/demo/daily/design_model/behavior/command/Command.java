package cn.zhanghui.demo.daily.design_model.behavior.command;

/**
 * 命令模式里面的命令
 * 这里比较统一的描述一个常规命令包含的内容，除了cancel外还会有redo等通用操作
 * @author ZhangHui
 * @date 2020/6/24
 */
public interface Command {

    void execute();

    void cancel();

}
