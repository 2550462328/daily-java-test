//数字 n 代表生成括号的对数，请你设计一个函数，用于能够生成所有可能的并且 有效的 括号组合。 
//
// 
//
// 示例 1： 
//
// 
//输入：n = 3
//输出：["((()))","(()())","(())()","()(())","()()()"]
// 
//
// 示例 2： 
//
// 
//输入：n = 1
//输出：["()"]
// 
//
// 
//
// 提示： 
//
// 
// 1 <= n <= 8 
// 
//
// Related Topics 字符串 动态规划 回溯 👍 3207 👎 0
package cn.zhanghui.demo.daily.leetcode.editor.cn;


import java.util.ArrayList;
import java.util.List;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {

    public List<String> generateParenthesis(int n) {
        List<String> ans = new ArrayList<String>();
        backtrack(ans, new StringBuilder(), 0, 0, n);
        return ans;
    }

    public void backtrack(List<String> ans, StringBuilder cur, int open, int close, int max) {
        if (cur.length() == max * 2) {
            ans.add(cur.toString());
            return;
        }
        if (open < max) {
            cur.append('(');
            backtrack(ans, cur, open + 1, close, max);
            cur.deleteCharAt(cur.length() - 1);
        }
        if (close < open) {
            cur.append(')');
            backtrack(ans, cur, open, close + 1, max);
            cur.deleteCharAt(cur.length() - 1);
        }
    }


    public List<String> generateParenthesis1(int n) {
        List<String> resultList = new ArrayList<>();
        getSb("", resultList, n, n);

        return resultList;
    }

    private void getSb(String last, List<String> resultList, int leftQueue, int rightQueue) {
        if (rightQueue == 0) {
            resultList.add(last);
            return;
        }
        if (leftQueue == rightQueue) {
            getSb(last + "(", resultList, leftQueue - 1, rightQueue);
        } else if (leftQueue == 0) {
            getSb(last + ")", resultList, leftQueue, rightQueue - 1);
        } else {
            getSb(last + "(", resultList, leftQueue - 1, rightQueue);
            getSb(last + ")", resultList, leftQueue, rightQueue - 1);
        }
    }
}
//leetcode submit region end(Prohibit modification and deletion)
