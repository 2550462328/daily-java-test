package cn.zhanghui.demo.daily.design_model.behavior.command;

/**
 * @author ZhangHui
 * @version 1.0
 * @className TelCommand
 * @description 命运模式里的具体命令
 * 这里封装的一些对电视的命令
 * @date 2020/6/24
 */
public class TelCommand implements Command {

    private Television television;

    public TelCommand(Television television) {
        this.television = television;
    }

    @Override
    public void execute() {
        this.television.open();
        //其他操作
    }

    @Override
    public void cancel() {
        this.television.off();
    }
}
