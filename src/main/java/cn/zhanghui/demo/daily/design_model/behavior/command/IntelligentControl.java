package cn.zhanghui.demo.daily.design_model.behavior.command;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZhangHui
 * @version 1.0
 * @className RemoteController
 * @description 命令模式的请求者
 *              这里我们设计的是一款只能遥控器，当然一代遥控器功能不会很多，目前仅支持空调和电视的开关功能
 * @date 2020/6/24
 */
public class IntelligentControl {

    private Map<Integer, Command> commandMap;


    public IntelligentControl(int capability){
        commandMap = new HashMap<>(capability);
    }

    /**
     * 安装可用命令
     * @author ZhangHui
     * @date 2020/6/24
     * @param pos 遥控器的孔位
     * @param command 孔位对应的功能 比如 1孔位打开空调 2孔位 打开电视等
     * @return void
     */
    public void setCommand(int pos, Command command){
        commandMap.put(pos,command);
    }

    public void onButton(int pos){
        Command command = commandMap.get(pos);
        if(command != null){
            command.execute();
        }
    }

    public void offButton(int pos){
        Command command = commandMap.get(pos);
        if(command != null){
            command.cancel();
        }
    }
}
