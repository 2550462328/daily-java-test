package cn.zhanghui.demo.daily.design_model.createobj.prototype;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Main
 * @description 这是描述信息
 * @date 2020/6/22
 */
public class Main {
    public static void main(String[] args) {
        EcliseTool ecliseTool = new EcliseTool();
        IdeaTool ideaTool = new IdeaTool();

        EditManager manager = new EditManager();
        manager.registerTool("ecliseTool", ecliseTool);
        manager.registerTool("ideaTool", ideaTool);

        manager.getTool("ideaTool").use();
    }
}
