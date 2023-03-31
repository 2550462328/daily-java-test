package cn.zhanghui.demo.daily.base.algorithm.str;

/**
 * @author ZhangHui
 * @version 1.0
 * @className StringConvertToNumber
 * @description 将字符串转换成整数
 * <p>
 * 该函数会根据需要丢弃无用的开头空格字符，直到寻找到第一个非空格的字符为止。
 * <p>
 * 假如该字符串中的第一个非空格字符不是一个有效整数字符、字符串为空或字符串仅包含空白字符时，则你的函数不需要进行转换，即无法进行有效转换。
 * <p>
 * 在任何情况下，若函数不能进行有效的转换时，请返回 0 。
 * <p>
 * 示例 2:
 * 输入: "   -42"
 * 输出: -42
 * <p>
 * 示例 4:
 * 输入: "words and 987"
 * 输出: 0
 * <p>
 * 示例 5:
 * 输入: "-91283472332"
 * 输出: -2147483648
 * @date 2020/9/11
 */
public class StringConvertToNumber {

    public static void main(String[] args) {
        StringConvertToNumber stringConvertToNumber = new StringConvertToNumber();

        String str = "0-1";

        System.out.println(stringConvertToNumber.myAtoiViolent(str));
    }

    /**
     * 核心采用StringBuilder暴力拼接数字
     */
    public int myAtoiViolent(String str) {

        int i = 0;

        if (str.length() == 0) {
            return 0;
        }

        StringBuilder sb = new StringBuilder();

        boolean hasNotZero = false;

        boolean zeroStart = false;

        char c;

        while (i < str.length()) {
            c = str.charAt(i);
            if (!zeroStart && sb.length() == 0) {
                if (c == '+' || c == '-' || (Character.isDigit(c) && c > '0')) {
                    sb.append(c);
                    if ((Character.isDigit(c) && c > '0')) {
                        hasNotZero = true;
                    }
                } else if (c == '0') {
                    zeroStart = true;
                } else if (c != ' ' && c != '0') {
                    return 0;
                }
            } else if (Character.isDigit(c)) {
                if (!hasNotZero && c > '0') {
                    hasNotZero = true;
                }
                if (hasNotZero) {
                    sb.append(c);
                    if (sb.length() > 11) {
                        if (sb.indexOf("-") != -1) {
                            return Integer.MIN_VALUE;
                        } else {
                            return Integer.MAX_VALUE;
                        }
                    }
                }
            } else {
                break;
            }
            i++;
        }

        String sbString = sb.toString();

        if (sbString.length() == 0 || "+".equals(sbString) || "-".equals(sbString)) {
            return 0;
        }

        long sbLong = Long.valueOf(sbString);

        if (sbLong > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else if (sbLong < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        } else {
            return (int) sbLong;
        }
    }
}
