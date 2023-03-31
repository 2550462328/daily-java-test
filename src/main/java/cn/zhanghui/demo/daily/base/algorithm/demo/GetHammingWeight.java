package cn.zhanghui.demo.daily.base.algorithm.demo;

/**
 * 编写一个函数，输入是一个无符号整数，返回其二进制表达式中数字位数为 ‘1’ 的个数（也被称为汉明重量）。
 * <p>
 * 示例 1：
 * <p>
 * 输入：00000000000000000000000000001011
 * 输出：3
 * 解释：输入的二进制串 00000000000000000000000000001011 中，共有三位为 '1'。
 *
 * @author: ZhangHui
 * @date: 2020/10/22 9:20
 * @version：1.0
 */
public class GetHammingWeight {

    public static void main(String[] args) {
        int i = -5;
//        System.out.println(Integer.toString(i,2)); // -101 有符号 有无符号位就是有无+ -
//        String s = Integer.toBinaryString(i);
//        System.out.println(s); //  11111111111111111111111111111011 无符号位 符号位被转换成0 1 负号的补码形式
//        System.out.println(Integer.parseUnsignedInt(s,2));  //  -5 解析无符号位
//        System.out.println(Integer.parseInt(s,2)); // 解析有符号位 必须有 + - 号
    }

    /**
     * 借助Integer.toBinaryString 投机取巧
     */
    public int hammingWeight(int n) {
        String s = Integer.toBinaryString(n);
        int count = 0;

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '1') {
                count++;
            }
        }
        return count;
    }

    /**
     * 将n一步步的向右位移，然后与1做位与即可
     */
    public int hammingWeight_movestep(int n) {
        int count = 0;

        for (int i = 0; i < 32; i++) {
            if ((n & 1) != 0) {
                count++;
            }
            n = n >> 1;
        }

        return count;
    }

}
