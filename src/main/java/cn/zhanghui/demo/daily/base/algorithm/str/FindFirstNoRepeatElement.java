package cn.zhanghui.demo.daily.base.algorithm.str;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZhangHui
 * @version 1.0
 * @className FindFirstNoRepeatElement
 * @description 给定一个字符串，找到它的第一个不重复的字符，并返回它的索引。如果不存在，则返回 -1。
 * <p>
 * 示例：
 * s = "leetcode"
 * 返回 0
 * <p>
 * s = "loveleetcode"
 * 返回 2
 * @date 2020/9/10
 */
public class FindFirstNoRepeatElement {

    public static void main(String[] args) {
        FindFirstNoRepeatElement findFirstNoRepeatElement = new FindFirstNoRepeatElement();
        System.out.println(findFirstNoRepeatElement.firstUniqCharViolent("cc"));
    }

    /**
     * 使用HashMap解决查找和查重
     */
    public int firstUniqCharHashMap(String s) {

        char[] chars = s.toCharArray();

        if (chars.length == 1) {
            return 0;
        }

        Map<Character, Integer> map = new HashMap();

        for (int i = 0; i < chars.length; i++) {
            if (map.containsKey(chars[i])) {
                map.put(chars[i], map.get(chars[i]) + 1);
            } else {
                map.put(chars[i], 1);
            }
        }

        for (int i = 0; i < chars.length; i++) {
            if (map.get(chars[i]) == 1) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 从前往后和从后往前查找
     */
    public int firstUniqCharViolent(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.lastIndexOf(s.charAt(i)) == i && s.indexOf(s.charAt(i)) == i) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 效率较好（如果字符串不是在a~z之间的话，此题用char[256]效率最好）
     * 这里就用a~z的字符数组求解
     * 非常巧妙，利用char - 'a'即可算出在char[26]上的位置，在char[256]的基础上进行了一次强有力的优化
     */
    public int firstUniqCharWithCharArray1(String s) {
        int[] chars = new int[26];
        for (char c : s.toCharArray()) {
            chars[c - 'a'] += 1;
        }
        for (int i = 0; i < s.length(); ++i) {
            if (chars[s.charAt(i) - 'a'] == 1) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 效率最好
     * 进一步进行优化，上述基于char数组的操作还是需要进行二次遍历
     * 这次优化内容就是在一次遍历中解决问题，不遍历数组，转而遍历a~z的字符，并求出符合条件最靠前位置的下标
     */
    public int firstUniqCharWithCharArray2(String s) {
        //只遍历26个字母，使用indexOf函数检查字符索引
        int result = -1;
        for (char c = 'a'; c <= 'z'; ++c) {
            int pre = s.indexOf(c);
            // s包含该字符并且只出现一次
            if (pre != -1 && pre == s.lastIndexOf(c)) {
                // 取最前面的位置
                result = (result == -1 || result > pre) ? pre : result;
            }
        }
        return result;
    }

}
