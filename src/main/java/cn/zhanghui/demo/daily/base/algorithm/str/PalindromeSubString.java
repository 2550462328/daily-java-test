package cn.zhanghui.demo.daily.base.algorithm.str;

/**
 * @author ZhangHui
 * @version 1.0
 * @className PalindromeSubString
 * @description 给定一个字符串s，找到其中最长的回文子序列。可以假设s的最大长度为1000。 最长回文子序列和上一题最长回文子串的区别是，子串是字符串中连续的一个序列，而子序列是字符串中保持相对位置的字符序列，例如，"bbbb"可以是字符串"bbbab"的子序列但不是子串。
 * <p>
 * 解决方案：使用动态规划思想，求解最大回文子序列
 * @date 2020/7/30
 */
public class PalindromeSubString {

    public int longestSubString(String target) {

        int len = target.length();

        int[][] dp = new int[len][len];

        for (int i = len - 1; i >= 0; i--) {
            dp[i][i] = 1;

            for(int j = i + 1; j < len; j++){

                // 动态规划思想，求的最优解是建立于之前的计算基础上的，比如dp[0][1] = dp[0][0] + n
                // 所以这里dp[i][j]的最优解是在dp[i+1][j-1]的基础上，在charAt(i) 和charAt(j)相等情况下，相当于最优解 + 1 + 1
                if(target.charAt(i) == target.charAt(j)){
                    dp[i][j] = dp[i+1][j-1] + 2;
                }else{
                    dp[i][j] = Math.max(dp[i+1][j],dp[i][j-1]);
                }
            }
        }
        return dp[0][len-1];
    }


}
