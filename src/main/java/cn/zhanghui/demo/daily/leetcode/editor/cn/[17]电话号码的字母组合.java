//给定一个仅包含数字 2-9 的字符串，返回所有它能表示的字母组合。答案可以按 任意顺序 返回。 
//
// 给出数字到字母的映射如下（与电话按键相同）。注意 1 不对应任何字母。 
//
// 
//
// 
//
// 示例 1： 
//
// 
//输入：digits = "23"
//输出：["ad","ae","af","bd","be","bf","cd","ce","cf"]
// 
//
// 示例 2： 
//
// 
//输入：digits = ""
//输出：[]
// 
//
// 示例 3： 
//
// 
//输入：digits = "2"
//输出：["a","b","c"]
// 
//
// 
//
// 提示： 
//
// 
// 0 <= digits.length <= 4 
// digits[i] 是范围 ['2', '9'] 的一个数字。 
// 
//
// Related Topics 哈希表 字符串 回溯 👍 2431 👎 0

package cn.zhanghui.demo.daily.leetcode.editor.cn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//leetcode submit region begin(Prohibit modification and deletion)

class Solution17 {
    public static final String[] s[] = {{"a", "b", "c"}, {"d", "e", "f"}, {"g", "h", "i"}, {"j", "k", "l"}, {"m", "n", "o"}, {"p", "q", "r", "s"}, {"t", "u", "v"}, {"w", "x", "y", "z"}};

    /**
     * 回溯解法（递归）
     * 下一个递归的值 依赖上一个递归值
     *
     * @param digits
     * @return
     */
    public List<String> letterCombinations(String digits) {
        List<String> list = new ArrayList<>();
        if(digits.length() == 0){
            return list;
        }
        list.addAll(Arrays.asList(merge(digits,1,s[digits.charAt(0) - '2'])));
        return list;
    }

    private String[] merge(String digits, int i, String[] last) {
        if(i >= digits.length()){
            return last;
        }
        String[] s1 = s[digits.charAt(i) - '2'];
        String[] res = new String[s1.length * last.length];
        int index = 0;
        for (String s : last) {
            for (String ss : s1) {
                res[index++] = s + ss;
            }
        }
        return merge(digits, ++i, res);
    }
}
//leetcode submit region end(Prohibit modification and deletion)
