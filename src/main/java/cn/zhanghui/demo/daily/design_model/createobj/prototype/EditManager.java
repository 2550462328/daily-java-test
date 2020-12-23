package cn.zhanghui.demo.daily.design_model.createobj.prototype;

import java.util.HashMap;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Manager
 * @description 原型模式中的Manager
 * @date 2020/6/22
 */
public class EditManager {
    private HashMap<String, Tool> toolMap = new HashMap<>();

    public void registerTool(String name, Tool tool){
        this.toolMap.put(name,tool);
    }

    public Tool getTool(String name){
        return this.toolMap.get(name);
    }
}
