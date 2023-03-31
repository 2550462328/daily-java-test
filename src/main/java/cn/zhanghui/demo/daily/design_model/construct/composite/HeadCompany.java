package cn.zhanghui.demo.daily.design_model.construct.composite;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Company
 * @description 总公司
 * @date 2020/6/23
 */
public class HeadCompany implements Orginization {

    private Set<Orginization> orginizationSet = new HashSet<>();

    @Override
    public void add(Orginization orginization) {
        orginizationSet.add(orginization);
    }

    @Override
    public void remove(Orginization orginization) {
        orginizationSet.remove(orginization);
    }

    @Override
    public void display() {
        orginizationSet.forEach(i -> i.run());
    }

    @Override
    public void run() {
        System.out.println("我是总公司，我要努力赚钱养活我的员工~");
    }
}
