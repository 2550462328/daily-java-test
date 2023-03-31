package cn.zhanghui.demo.daily.jdk8_newProp.concurrenthashmap;


import java.util.HashMap;
import java.util.Map;

public class ConcurrentHashMapTest {
    public static void main(String[] args) {
        ConcurrentHashMapDebug<String, String> map = new ConcurrentHashMapDebug<>(2);
        Map<String, String> hashMap = new HashMap<>();
        for (int i = 0; i < 20; i++) {
            hashMap.put("key" + i, "value" + i);
        }
        System.out.println("i have done！");
        map.putAll(hashMap);
    }
}
