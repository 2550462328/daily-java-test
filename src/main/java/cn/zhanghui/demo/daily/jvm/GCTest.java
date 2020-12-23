package cn.zhanghui.demo.daily.jvm;

/**
 * @author ZhangHui
 * @version 1.0
 * @className GCTest
 * @description 这是描述信息
 * @date 2020/5/13
 */
public class GCTest {

    public static void main(String[] args) {
//        byte[] allocate1 = new byte[27900*1024];
//        byte[] allocate2 = new byte[900*1024];
        String s = new String("aaaaa");

    }
}
