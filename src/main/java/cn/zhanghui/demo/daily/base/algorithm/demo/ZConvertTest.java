package cn.zhanghui.demo.daily.base.algorithm.demo;

import java.util.Objects;

/**
 * @author ZhangHui
 * @version 1.0
 * @className ZConvertTest
 * @description 将一个给定字符串根据给定的行数，以从上往下、从左到右进行 Z 字形排列。
 *
 * 比如输入字符串为 "LEETCODEISHIRING" 行数为 3 时，排列如下：
 *
 * L   C   I   R
 * E T O E S I I G
 * E   D   H   N
 *
 * 之后，你的输出需要从左往右逐行读取，产生出一个新的字符串，比如："LCIRETOESIIGEDHN"。
 *
 *  解题思路：
 *
 * 对于n行的, s中的第i个字符：
 * 对余数进行判断
 * i%(2n-2) == 0 ----> row0
 * i%(2n-2) == 1 & 2n-2-1 ----> row1
 * i%(2n-2) == 2 & 2n-2-2 ----> row2
 * ...
 * i%(2n-2) == n-1 ----> row(n-1)
 * ==>
 * 对 k = i%(2n-2)进行判断
 * k<=n-1时候，s[i]就属于第k行
 * k>n-1时候，s[i]就属于2n-2-k行
 * 最后将rows拼接起来就行了
 *
 * @date 2020/8/24
 */
public class ZConvertTest {

    public static void main(String[] args) {
        String target = "LEETCODEISHIRING";

        System.out.println(convertToZType(target,3));

        System.out.println(Objects.equals("LCIRETOESIIGEDHN",convertToZType(target,3)));
    }

    public static String convertToZType(String target, int depth){
        if (depth == 1) return target;
        StringBuilder sb = new StringBuilder();
        int n = target.length();
        for (int i = 0; i < depth; i++) {
            for (int j = i; j < n; j += (depth - 1) * 2) {
                sb.append(target.charAt(j));
                if (i > 0 && i < depth - 1) {
                    int sec = j + 2 * (depth - i - 1);
                    if (sec < n) {
                        sb.append(target.charAt(sec));
                    }
                }
            }
        }
        return sb.toString();
    }
}
