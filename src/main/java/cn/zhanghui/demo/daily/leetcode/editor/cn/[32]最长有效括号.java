//给你一个只包含 '(' 和 ')' 的字符串，找出最长有效（格式正确且连续）括号子串的长度。 
//
// 
//
// 
// 
// 示例 1： 
// 
// 
//
// 
//输入：s = "(()"
//输出：2
//解释：最长有效括号子串是 "()"
// 
//
// 示例 2： 
//
// 
//输入：s = ")()())"
//输出：4
//解释：最长有效括号子串是 "()()"
// 
//
// 示例 3： 
//
// 
//输入：s = ""
//输出：0
// 
//
// 
//
// 提示： 
//
// 
// 0 <= s.length <= 3 * 10⁴ 
// s[i] 为 '(' 或 ')' 
// 
//
// Related Topics 栈 字符串 动态规划 👍 2265 👎 0

package cn.zhanghui.demo.daily.leetcode.editor.cn;

import java.util.Deque;
import java.util.LinkedList;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    /**
     * 动态规划 计算从i的起点往前的最大跨度
     * 需要考虑两种场景 1）i为) 且 i-1为( 2) i为) 且 i - dp[i-1] 为(
     *
     * @param s
     * @return
     */
    public int longestValidParentheses3(String s) {
        int maxans = 0;
        int[] dp = new int[s.length()];
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == ')') {
                if (s.charAt(i - 1) == '(') {
                    dp[i] = (i >= 2 ? dp[i - 2] : 0) + 2;
                } else if (i - dp[i - 1] > 0 && s.charAt(i - dp[i - 1] - 1) == '(') {
                    dp[i] = dp[i - 1] + ((i - dp[i - 1]) >= 2 ? dp[i - dp[i - 1] - 2] : 0) + 2;
                }
                maxans = Math.max(maxans, dp[i]);
            }
        }
        return maxans;
    }

    /**
     * 一次循环 入栈/出栈 始终计算 （和)之间的最大跨度
     *
     * @param s
     * @return
     */
    public int longestValidParentheses2(String s) {
        int maxans = 0;
        Deque<Integer> stack = new LinkedList<Integer>();
        stack.push(-1);
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                stack.push(i);
            } else {
                stack.pop();
                if (stack.isEmpty()) {
                    stack.push(i);
                } else {
                    maxans = Math.max(maxans, i - stack.peek());
                }
            }
        }
        return maxans;
    }

    /**
     * 对每个下标都做 入栈/出栈判断是否符合
     *
     * @param s
     * @return
     */
    public int longestValidParentheses1(String s) {
        int maxLength = 0;
        out:
        for (int i = 0; i < s.length() - 1; i++) {
            LinkedList<Character> stack = new LinkedList<>();
            int k = i;
            for (int j = i; j < s.length(); j++) {
                if (!pushStack(stack, s.charAt(j))) {
                    maxLength = Math.max(maxLength, j - i);
                    continue out;
                } else if (stack.size() == 0) {
                    k = j;
                }
            }
            if (k != i) {
                maxLength = Math.max(maxLength, k - i + 1);
            }
        }
        return maxLength;
    }

    private boolean pushStack(LinkedList<Character> list, Character c) {
        if (c == '(') {
            list.addFirst(c);
        } else if (list.peek() != null && list.peek() == '(') {
            list.poll();
        } else {
            return false;
        }
        return true;
    }
}
//leetcode submit region end(Prohibit modification and deletion)
