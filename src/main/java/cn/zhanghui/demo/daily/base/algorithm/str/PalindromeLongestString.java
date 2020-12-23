package cn.zhanghui.demo.daily.base.algorithm.str;

/**
 * @author ZhangHui
 * @version 1.0
 * @className PalindromeString
 * @description 给定一个字符串 s，找到 s 中最长的回文子串。你可以假设 s 的最大长度为1000
 * @date 2020/7/30
 */
public class PalindromeLongestString {

    private int pLen;
    private int pIndex;

    /**
     * 以一个元素为中心，计算偶数回文串和奇数回文串的情况
     */
    public String getLongest(String target) {

        int m = target.length();

        if (m < 2) {
            return target;
        }
        char[] chars = target.toCharArray();

        for (int i = 0; i < m; i++) {
            computeHelper(i, i, chars);
            computeHelper(i, i + 1, chars);
        }

        return target.substring(pIndex, pIndex + pLen);
    }

    /**
     * 计算字符数组在左起点l和右起点r的回文串长度
     *
     * @param l     左起点
     * @param r     右起点
     * @param chars 目标字符数组
     */
    private void computeHelper(int l, int r, char[] chars) {

        while (l >= 0 && r < chars.length && chars[l] == chars[r]) {
            l--;
            r++;
        }

        // 取当前回文串长度和历史最大长度的较大值
        if (pLen < r - l - 1) {
            pLen = r - l - 1;
            pIndex = l + 1;
        }
    }

    /**
     * 通过动态规划进行求解，以不同长度和不同起点为分界线
     * i~j能否是回文串取决于i+1~j-1 是否是回文串，且 target.charAt(i) == target.charAt(j)
     * 当然对于动态规划问题需要注意边界值 比如dp[i][i] = true
     */
    public String getLongest_db(String target) {

        int n = target.length();

        boolean[][] dp = new boolean[n][n];

        String ans = "";

        for (int l = 0; l < n; l++) {

            for (int i = 0; i + l < n; i++) {

                int j = i + l;
                if (l == 0) {
                    dp[i][j] = true;
                } else if (l == 1) {
                    dp[i][j] = target.charAt(i) == target.charAt(j);
                } else {
                    dp[i][j] = (target.charAt(i) == target.charAt(j)) && dp[i + 1][j - 1];
                }

                if (dp[i][j] && l + 1 > ans.length()) {
                    ans = target.substring(i, j + 1);
                }
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        String target = "abcbagfh";

        System.out.println(new PalindromeLongestString().getLongest_db(target));
    }
}
