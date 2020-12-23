package cn.zhanghui.demo.daily.base.algorithm.demo;

import java.util.*;

/**
 * 给定一个只包括 '('，')'，'{'，'}'，'['，']' 的字符串，判断字符串是否有效。
 *
 * 有效字符串需满足：
 *
 * 	左括号必须用相同类型的右括号闭合。
 * 	左括号必须以正确的顺序闭合。
 *
 * 注意空字符串可被认为是有效字符串。
 *
 * 示例 1:
 *
 * 输入: "()"
 * 输出: true
 *
 * @author: ZhangHui
 * @date: 2020/11/4 8:45
 * @version：1.0
 */
public class ValidBracesStatement {

    private Map<Character, Character> map = new HashMap<>();

    {
        map.put('[', ']');
        map.put('{', '}');
        map.put('(', ')');
    }

    /**
     * 使用栈来判断字符串是否符合
     */
    public boolean isValid(String s) {

        if (s.length() % 2 == 1) {
            return false;
        }

        Deque<Character> stack = new LinkedList<>();

        for (int i = 0; i < s.length(); i++) {
            if (isMapKey(s.charAt(i))) {
                stack.push(s.charAt(i));
            } else if (stack.isEmpty() || map.get(stack.poll()) != s.charAt(i)) {
                return false;
            }
        }

        if (!stack.isEmpty()) {
            return false;
        }
        return true;
    }

    private boolean isMapKey(Character chr) {
        return map.keySet().contains(chr);
    }
}
