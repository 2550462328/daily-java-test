//给你一个字符串 s，找到 s 中最长的回文子串。 
//
// 如果字符串的反序与原始字符串相同，则该字符串称为回文字符串。 
//
// 
//
// 示例 1： 
//
// 
//输入：s = "babad"
//输出："bab"
//解释："aba" 同样是符合题意的答案。
// 
//
// 示例 2： 
//
// 
//输入：s = "cbbd"
//输出："bb"
// 
//
// 
//
// 提示： 
//
// 
// 1 <= s.length <= 1000 
// s 仅由数字和英文字母组成 
// 
//
// Related Topics 字符串 动态规划 👍 6367 👎 0
package cn.zhanghui.demo.daily.leetcode.editor.cn;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution5 {
    private int lsIndex;
    private int lsLength;

    public static void main(String[] args) {
        String s = "asa";
        System.out.println(new Solution5().longestPalindrome(s));
    }

    /**
     * 通过动态规划进行求解，以不同长度和不同起点为分界线
     * i~r能否是回文串取决于i+1~r-1 是否是回文串，且 chars(i) == chars(r)
     * 当然对于动态规划问题需要注意边界值 比如dp[i][i] = true
     */
    public String longestPalindrome(String s) {
        if (s == null || s.length() < 2) {
            return s;
        }
        int n = s.length();
        boolean[][] result = new boolean[n][n];

        int pLength = 0, pIndex = 0;
        char[] chars = s.toCharArray();

        for (int l = 0; l < n; l++) {
            for (int i = 0; i + l < n; i++) {
                int r = i + l;
                if (l == 0) {
                    result[i][r] = true;
                } else if (l == 1) {
                    result[i][r] = chars[i] == chars[r];
                } else {
                    result[i][r] = chars[i] == chars[r] && result[i + 1][r - 1];
                }
                if (l > pLength && result[i][r]) {
                    pLength = l;
                    pIndex = i;
                }
            }
        }
        return s.substring(pIndex, pIndex + pLength + 1);
    }

    /**
     * 以一个元素为中心，计算偶数回文串和奇数回文串的情况
     */
    public String longestPalindrome1(String s) {
        if (s == null || s.length() < 2) {
            return s;
        }
        char[] chars = s.toCharArray();
        int index = 0, length = chars.length;
        while (index < length) {
            getLastStr(index, index, chars);
            getLastStr(index, index + 1, chars);
            index++;
        }
        return s.substring(lsIndex, lsIndex + lsLength);
    }

    /**
     * 计算字符数组在左起点l和右起点r的回文串长度
     *
     * @param left  左起点
     * @param right 右起点
     * @param chars 目标字符数组
     */
    private void getLastStr(int left, int right, char[] chars) {
        while (left >= 0 && right < chars.length && chars[left] == chars[right]) {
            left--;
            right++;
        }
        if (lsLength < right - left - 1) {
            lsLength = right - left - 1;
            lsIndex = left + 1;
        }
    }
}
//leetcode submit region end(Prohibit modification and deletion)
