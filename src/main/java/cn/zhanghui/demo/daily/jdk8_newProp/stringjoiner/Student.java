package cn.zhanghui.demo.daily.jdk8_newProp.stringjoiner;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONType;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Student
 * @description StringJoiner和fastjson中出现stackoverflow的联合测试
 * 字符串少的情况用 + 连接
 * for循环中用stringBuilder连接
 * 通过list则考虑使用stringJoiner同时配合stream
 * @date 2019/11/15
 */
@JSONType(ignores = "jsonString")
public class Student {
    /**
     * name :
     * age :
     */
    private String name;
    private String age;

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    // 序列化的时候不能序列化jsonString，会报StackOverflow
//    @JSONField(serialize = false)
    public String getJsonString() {
        return JSONObject.toJSONString(this);
    }

    public static void main(String[] args) {
        Student student = new Student();
        student.setName("zhanghui");
        student.setAge("18");
        System.out.println(JSONObject.toJSONString(student));
        System.out.println(student);

        List<String> list = new ArrayList<>();
        list.add("zhanghui");
        list.add("18");
        // 传统写法，这种方式不能直接得到
        System.out.println(list.stream().reduce((a, b) -> a + "," + b).toString());

        //利用StringJoiner的写法，Collectors.joining的底层用的就是StringJoiner
        System.out.println(list.stream().collect(Collectors.joining(":")).toString());
    }

    @Override
    public String toString() {
        // 使用StringJoiner拼接toString
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        joiner.add(this.name).add(this.age);
        return joiner.toString();
    }

}
