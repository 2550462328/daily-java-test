package cn.zhanghui.demo.daily.base.collection.array;

/**
 * @author ZhangHui
 * @version 1.0
 * @className FindFacadeArray
 * @description 给定一个正整数 n（1 ≤ n ≤ 30），输出外观数列的第 n 项。
 * 注意：整数序列中的每一项将表示为一个字符串。
 * 「外观数列」是一个整数序列，从数字 1 开始，序列中的每一项都是对前一项的描述。前五项如下：
 * <p>
 * 1.     1
 * 2.     11
 * 3.     21
 * 4.     1211
 * 5.     111221
 * <p>
 * 第一项是数字 1
 * 描述前一项，这个数是 1 即 “一个 1 ”，记作 11
 * 描述前一项，这个数是 11 即 “两个 1 ” ，记作 21
 * 描述前一项，这个数是 21 即 “一个 2 一个 1 ” ，记作 1211
 * 描述前一项，这个数是 1211 即 “一个 1 一个 2 两个 1 ” ，记作 111221
 * @date 2020/9/16
 */
public class FindFacadeArray {

    public static void main(String[] args) {
        FindFacadeArray findFacadeArray = new FindFacadeArray();

        for (int i = 1; i <= 5; i++) {
            System.out.println(findFacadeArray.countAndSay_recursion(i));
        }
    }

    /**
     * 使用传统递归解决
     */
    public String countAndSay_recursion(int n) {

        String s0 = "1";

        if (n == 1) {
            return s0;
        }

        // 从2开始
        int index = 1;
        String result = s0;

        while (index < n) {
            result = describeNum(result);
            index++;
        }
        return result;
    }

    private String describeNum(String target) {
        StringBuilder sb = new StringBuilder();

        int repeatTime = 1;
        int len = target.length();

        for (int i = 0; i < len; i++) {
            if (i != len - 1 && target.charAt(i) == target.charAt(i + 1)) {
                repeatTime++;
            } else {
                sb.append(repeatTime).append(target.charAt(i));
                repeatTime = 1;
            }
        }
        return sb.toString();
    }

}
