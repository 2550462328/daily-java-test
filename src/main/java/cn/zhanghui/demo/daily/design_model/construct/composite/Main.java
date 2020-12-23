package cn.zhanghui.demo.daily.design_model.construct.composite;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Main
 * @description 这是描述信息
 * @date 2020/6/23
 */
public class Main {
    public static void main(String[] args) {
        Orginization headCompany = new HeadCompany();
        Orginization branchCompany = new BranchCompany();
        headCompany.add(branchCompany);
        headCompany.add(new ResearchDepart());
        headCompany.add(new SalesDepart());

        branchCompany.add(new ResearchDepart());
        branchCompany.add(new SalesDepart());

        headCompany.display();
        branchCompany.display();
    }
}
