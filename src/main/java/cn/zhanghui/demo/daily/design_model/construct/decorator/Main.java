package cn.zhanghui.demo.daily.design_model.construct.decorator;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Main
 * @description 这是描述信息
 * @date 2020/6/23
 */
public class Main {

    public static void main(String[] args) {

        //不建议使用这种实例方式
        //DancerPerson dancerPerson = new DancerPerson(new HasTalentPeople(new CommonPerson()));

        // 推荐这种
        // 会跳舞的
        Person dancerPerson = new DancerPerson(new HasTalentPeople(new CommonPerson()));
        dancerPerson.eat();
        ((DancerPerson) dancerPerson).dance();

        System.out.println("===================");

        // 会弹钢琴的
        Person pianistPerson = new PianistPerson(new HasTalentPeople(new CommonPerson()));
        pianistPerson.eat();
        ((PianistPerson) pianistPerson).play();

        System.out.println("===================");

        // 这是一个全能儿
        Person allPerson = new PianistPerson(new DancerPerson(new HasTalentPeople(new CommonPerson())));
        allPerson.eat();
    }
}
