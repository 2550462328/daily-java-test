package cn.zhanghui.demo.daily.design_model.behavior.dutychain;

/**
 * @author ZhangHui
 * @version 1.0
 * @className ProjectManager
 * @description 责任链模式中的具体抽象处理角色
 * @date 2020/6/24
 */
public class GeneralManager extends Handler {

    @Override
    protected void handleRequest(int price) {
        if (Math.min(2000, price) == price) {
            System.out.println("总经理审批通过~");
        } else {
            Handler handler = this.getSuccessor();
            handler.handleRequest(price);
        }
    }

}
