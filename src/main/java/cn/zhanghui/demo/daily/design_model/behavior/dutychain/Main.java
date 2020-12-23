package cn.zhanghui.demo.daily.design_model.behavior.dutychain;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Main
 * @description 这是描述信息
 * @date 2020/6/24
 */
public class Main {
    public static void main(String[] args) {
        ProjectManager projectManager = new ProjectManager();
        DeptManager deptManager = new DeptManager();
        GeneralManager generalManager = new GeneralManager();

        projectManager.setSuccessor(deptManager);
        deptManager.setSuccessor(generalManager);

        projectManager.handleRequest(200);
    }
}
