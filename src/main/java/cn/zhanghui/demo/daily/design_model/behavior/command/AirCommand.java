package cn.zhanghui.demo.daily.design_model.behavior.command;

/**
 * @author ZhangHui
 * @version 1.0
 * @className TelCommand
 * @description 命运模式里的具体命令
 *              这里封装一些对空调的命令
 * @date 2020/6/24
 */
public class AirCommand implements Command {

    private AirConditional airConditional;

    public AirCommand(AirConditional airConditional){
        this.airConditional = airConditional;
    }

    @Override
    public void execute() {
        this.airConditional.open();
        //其他操作
    }

    @Override
    public void cancel() {
        this.airConditional.off();
    }
}
