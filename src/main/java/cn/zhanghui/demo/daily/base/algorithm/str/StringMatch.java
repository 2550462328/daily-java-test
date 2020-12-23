package cn.zhanghui.demo.daily.base.algorithm.str;

import java.util.Scanner;

/**
 * @author ZhangHui
 * @version 1.0
 * @className StringMatch
 * @description 给出两个字符串，分别是模式串P和目标串T，判断模式串和目标串是否匹配，匹配输出 1，不匹配输出 0。
 * 模式串中‘？’可以匹配目标串中的任何字符，模式串中的 ’*’可以匹配目标串中的任何长度的串，模式串的其它字符必须和目标串的字符匹配。
 * 例如P=a?b，T=acb，则P 和 T 匹配。
 * 这里额外添加了全匹配和局部匹配功能
 * 这里将*作为至少占用一个位置处理
 * @date 2020/7/23
 */
public class StringMatch {

    /**
     * 伪正则匹配
     *
     * @param target  待匹配内容
     * @param pattern 匹配格式
     * @param isAll   是否全部匹配（开头和结尾是否必须匹配）
     * @return int 1 匹配 0 不匹配
     */
    public static int match(String target, String pattern, boolean isAll) {
        int pLen = pattern.length();
        int tLen = target.length();

        // 矩形图
        boolean[][] dp = new boolean[tLen][pLen];

        // 先解决第一列特殊值，因为后面的推导需要依赖第一列的值
        for (int k = 0; k < tLen; k++) {
            if (isAll && k > 0) {
                dp[k][0] = false;
            } else {
                dp[k][0] = pattern.charAt(0) == '*' || pattern.charAt(0) == '?' || pattern.charAt(0) == target.charAt(k);
            }
        }

        // 遍历值，核心在于区分isAll和非isAll的场景
        for (int j = 1; j < pLen; j++) {
            for (int i = j; i < tLen; i++) {
                if (isAll && !dp[j - 1][j - 1]) {
                    dp[i][j] = false;
                } else if (!isAll && !dp[i - 1][j - 1]) {
                    dp[i][j] = false;
                } else {
                    dp[i][j] = pattern.charAt(j) == '*' || pattern.charAt(j) == '?' || pattern.charAt(j) == target.charAt(i);
                }
            }
        }

//        for(int i = 0; i < dp.length;i++){
//            System.out.println(Arrays.toString(dp[i]));
//        }

        // 特殊值判断返回结果
        if (isAll) {
            return dp[tLen - 1][pLen - 1] ? 1 : 0;
        } else {
            for (int i = 0; i < tLen; i++) {
                if (dp[i][pLen - 1]) {
                    return 1;
                }
            }
            return 0;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String p = sc.next();
        String s = sc.next();
        System.out.println(match(s,  p, true));
    }
}
