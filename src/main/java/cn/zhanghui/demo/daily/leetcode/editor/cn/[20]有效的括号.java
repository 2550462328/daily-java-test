//给定一个只包括 '('，')'，'{'，'}'，'['，']' 的字符串 s ，判断字符串是否有效。 
//
// 有效字符串需满足： 
//
// 
// 左括号必须用相同类型的右括号闭合。 
// 左括号必须以正确的顺序闭合。 
// 每个右括号都有一个对应的相同类型的左括号。 
// 
//
// 
//
// 示例 1： 
//
// 
//输入：s = "()"
//输出：true
// 
//
// 示例 2： 
//
// 
//输入：s = "()[]{}"
//输出：true
// 
//
// 示例 3： 
//
// 
//输入：s = "(]"
//输出：false
// 
//
// 
//
// 提示： 
//
// 
// 1 <= s.length <= 10⁴ 
// s 仅由括号 '()[]{}' 组成 
// 
//
// Related Topics 栈 字符串 👍 3895 👎 0

package cn.zhanghui.demo.daily.leetcode.editor.cn;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution20 {

    private static final Map<Character, Character> map = new HashMap<Character, Character>() {{
        put('{', '}');
        put('[', ']');
        put('(', ')');
    }};

    public boolean isValid(String s) {

        int i = 0;

        LinkedList<Character> stack = new LinkedList<>();
        while (i < s.length()) {
            char cur = s.charAt(i);
            if (map.containsKey(cur)) {
                stack.addFirst(cur);
            } else if (stack.size() == 0 || map.get(stack.getFirst()) != cur) {
                return false;
            } else {
                stack.pollFirst();
            }
            i++;
        }
        return stack.size() == 0;
    }
}
//leetcode submit region end(Prohibit modification and deletion)
