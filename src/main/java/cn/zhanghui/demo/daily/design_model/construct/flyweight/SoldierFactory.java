package cn.zhanghui.demo.daily.design_model.construct.flyweight;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ZhangHui
 * @version 1.0
 * @className SoldierFactory
 * @description 这是享元模式的享元工厂
 * 这是我们的兵工厂，现在开始生产兵种吧
 * 注意弓箭手和步兵是可以重复建造的，但是将军是不可重复建造的，嗯，他需要氪金才能得到
 * @date 2020/6/23
 */
public class SoldierFactory {

    private ConcurrentHashMap<String, Soldier> soldierMap = new ConcurrentHashMap<>();

    public Soldier produceUnit(String name) throws ClassNotFoundException {
        if (soldierMap.get(name) == null) {
            synchronized (soldierMap) {
                if (soldierMap.get(name) == null) {
                    switch (name) {
                        case "infantry":
                            soldierMap.put("infantry", new Infantry("infantry"));
                            break;
                        case "archer":
                            soldierMap.put("archer", new Archer("archer"));
                            break;
                        default:
                            throw new ClassNotFoundException("不支持的军种" + name);
                    }
                }
            }
        }
        return soldierMap.get(name);
    }
}
