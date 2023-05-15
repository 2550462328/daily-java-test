//给你两个字符串 haystack 和 needle ，请你在 haystack 字符串中找出 needle 字符串的第一个匹配项的下标（下标从 0 开始）。
//如果 needle 不是 haystack 的一部分，则返回 -1 。 
//
// 
//
// 示例 1： 
//
// 
//输入：haystack = "sadbutsad", needle = "sad"
//输出：0
//解释："sad" 在下标 0 和 6 处匹配。
//第一个匹配项的下标是 0 ，所以返回 0 。
// 
//
// 示例 2： 
//
// 
//输入：haystack = "leetcode", needle = "leeto"
//输出：-1
//解释："leeto" 没有在 "leetcode" 中出现，所以返回 -1 。
// 
//
// 
//
// 提示： 
//
// 
// 1 <= haystack.length, needle.length <= 10⁴ 
// haystack 和 needle 仅由小写英文字符组成 
// 
//
// Related Topics 双指针 字符串 字符串匹配 👍 1859 👎 0

package cn.zhanghui.demo.daily.leetcode.editor.cn;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution28 {

    public static void main(String[] args) {
        String haystack = "mississippi";
        String needle = "issi";

        System.out.println(new Solution28().strStr(haystack, needle));
    }

    public int strStr(String haystack, String needle) {
        int sl = haystack.length(), nl = needle.length();
        if (sl == 0 || nl == 0 || sl < nl) return -1;

        int i = 0;
        while (i <= sl - nl) {
            int cx = i + nl - 1;
            int tx = nl - 1;
            if (cx >= sl) {
                return -1;
            }
            int step = 0;
            while (cx >= i + step) {
                if (haystack.charAt(cx) == needle.charAt(tx)) {
                    tx--;
                    cx--;
                } else {
                    step++;
                    cx = i + nl - 1;
                    tx = nl - 1 - step;
                }
            }
            if (step == 0) {
                return i;
            }
            i += step;
        }
        return -1;
    }
}
//leetcode submit region end(Prohibit modification and deletion)
