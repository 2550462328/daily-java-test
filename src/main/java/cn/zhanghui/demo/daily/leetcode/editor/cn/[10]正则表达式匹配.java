//给你一个字符串 s 和一个字符规律 p，请你来实现一个支持 '.' 和 '*' 的正则表达式匹配。
//
//
// '.' 匹配任意单个字符
// '*' 匹配零个或多个前面的那一个元素
//
//
// 所谓匹配，是要涵盖 整个 字符串 s的，而不是部分字符串。
//
// 示例 1： 
//
// 
//输入：s = "aa", p = "a"
//输出：false
//解释："a" 无法匹配 "aa" 整个字符串。
// 
//
// 示例 2: 
//
// 
//输入：s = "aa", p = "a*"
//输出：true
//解释：因为 '*' 代表可以匹配零个或多个前面的那一个元素, 在这里前面的元素就是 'a'。因此，字符串 "aa" 可被视为 'a' 重复了一次。
// 
//
// 示例 3： 
//
// 
//输入：s = "ab", p = ".*"
//输出：true
//解释：".*" 表示可匹配零个或多个（'*'）任意字符（'.'）。
// 
//
// 
//
// 提示： 
//
// 
// 1 <= s.length <= 20 
// 1 <= p.length <= 20 
// s 只包含从 a-z 的小写字母。 
// p 只包含从 a-z 的小写字母，以及字符 . 和 *。 
// 保证每次出现字符 * 时，前面都匹配到有效的字符 
// 
//
// Related Topics 递归 字符串 动态规划 👍 3556 👎 0

package cn.zhanghui.demo.daily.leetcode.editor.cn;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution10 {

    public boolean isMatch(String s, String p) {
        int m = s.length();
        int n = p.length();

        boolean dp[][] = new boolean[m + 1][n + 1];
        dp[0][0] = true;

        for (int i = 0; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (p.charAt(j-1) != '*') {
                    dp[i][j] = i > 0 && dp[i - 1][j - 1] && (s.charAt(i - 1) == p.charAt(j - 1) || p.charAt(j - 1) == '.');
                } else {
                    dp[i][j] = dp[i][j - 2] || (i > 0 && (s.charAt(i - 1) == p.charAt(j - 2) || p.charAt(j - 2) == '.') && dp[i - 1][j]);
                }
            }
        }

        return dp[m][n];
    }

    public boolean isMatch1(String s, String p) {
        char[] source = s.toCharArray();
        char[] pattern = p.toCharArray();

        int si = 0, pi = 0;
        if (pattern[pi] == '*') return false;

        return match(source, pattern, si, pi, ' ', false, false);
    }

    private boolean match(char[] source, char[] pattern, int si, int pi, char prev, boolean isNext, boolean isStarted) {
        if (pi >= pattern.length && (si >= source.length || isNext)) {
            return true;
        } else if (pi >= pattern.length || si >= source.length) {
            return false;
        }
        if (pattern[pi] == '.' || pattern[pi] == source[si]) {
            prev = pattern[pi];
            isStarted = true;
            return match(source, pattern, ++si, ++pi, prev, false, isStarted);
        } else if (pattern[pi] == '*' && (prev == '.' || source[si] == prev)) {
            return match(source, pattern, ++si, ++pi, prev, true, isStarted);
        } else if (pattern[pi] == '*') {
            prev = pattern[pi];
            return match(source, pattern, si, ++pi, prev, false, isStarted);
        } else if (isNext && (prev == '.' || source[si] == prev)) {
            return match(source, pattern, ++si, pi, prev, true, isStarted);
        } else if (!isStarted) {
            prev = pattern[pi];
            return match(source, pattern, ++si, ++pi, prev, false, false);
        } else {
            return false;
        }
    }


}
//leetcode submit region end(Prohibit modification and deletion)
