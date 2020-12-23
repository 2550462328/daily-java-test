package cn.zhanghui.demo.daily.base.algorithm.demo;

/**
 * @author ZhangHui
 * @version 1.0
 * @className NumberReverse
 * @description 给出一个 32 位的有符号整数，你需要将这个整数中每位上的数字进行反转。
 * <p>
 * 示例 1:
 * 输入: 123
 * 输出: 321
 * <p>
 * 示例 2:
 * 输入: -123
 * 输出: -321
 * <p>
 * <p>
 * 示例 3:
 * 输入: 120
 * 输出: 21
 * @date 2020/9/10
 */
public class NumberReverse {

    public static void main(String[] args) {
//        NumberReverse numberReverse = new NumberReverse();
//
//        int x = 1534236469;
//
//      System.out.println(numberReverse.reverse(x));
        System.out.println(Integer.valueOf("964632435"));
    }

    /**
     * 使用传统暴力求解 ，即调换数字的位置
     * 但需要注意临界值，比如负号开头、后缀是0
     * 当需要经历String字符串转换和分割 效率不高
     */
    public int reverseViolent(int x) {
        char[] s = String.valueOf(x).toCharArray();

        if (s.length < 2) {
            return x;
        }

        int i = s[0] == '-' ? 1 : 0;
        int j = s.length - 1;

        while (s[j] == '0') {
            j--;
        }

        int m = i, n = j;
        while (m < n) {
            char temp = s[m];
            s[m] = s[n];
            s[n] = temp;

            m++;
            n--;
        }

        int result = 0;
        try {
            result = Integer.valueOf(new String(s).substring(0, j + 1));
        } catch (NumberFormatException e) {
            return 0;
        }
        return result;
    }

    /**
     * 这里非常巧妙的将数字x分解，x % 10 得到尾部的值，x / 10 得到 x 去掉尾部的值，再依次递加x % 10 * 10的值即可得到最终解
     * 效率最高
     */
    public int reverse(int x) {
        long temp = 0;

        while (x != 0) {

            int pop = x % 10;

            temp = temp * 10 + pop;

            if (temp > Integer.MAX_VALUE || temp < Integer.MIN_VALUE) {
                return 0;
            }
            x /= 10;
        }

        return (int) temp;
    }

}
