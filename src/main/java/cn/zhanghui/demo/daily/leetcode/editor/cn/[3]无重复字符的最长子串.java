//给定一个字符串 s ，请你找出其中不含有重复字符的 最长子串 的长度。 
//
// 
//
// 示例 1: 
//
// 
//输入: s = "abcabcbb"
//输出: 3 
//解释: 因为无重复字符的最长子串是 "abc"，所以其长度为 3。
// 
//
// 示例 2: 
//
// 
//输入: s = "bbbbb"
//输出: 1
//解释: 因为无重复字符的最长子串是 "b"，所以其长度为 1。
// 
//
// 示例 3: 
//
// 
//输入: s = "pwwkew"
//输出: 3
//解释: 因为无重复字符的最长子串是 "wke"，所以其长度为 3。
//     请注意，你的答案必须是 子串 的长度，"pwke" 是一个子序列，不是子串。
// 
//
// 
//
// 提示： 
//
// 
// 0 <= s.length <= 5 * 10⁴ 
// s 由英文字母、数字、符号和空格组成 
// 
//
// Related Topics 哈希表 字符串 滑动窗口 👍 8984 👎 0
package cn.zhanghui.demo.daily.leetcode.editor.cn;

import java.util.HashSet;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution3 {
        public int lengthOfLongestSubstring(String s) {
            int ans = 0;
            // 用ASII码代替HashSet判断重复 和 记录重复下标
            int[] arr = new int[130];
            // c为最后一次出现重复数的下标+1
            int c = 0;
            for (int i = 0; i < s.length(); i++) {
                // c < arr[s.charAt(i)]保证c只能增大
                if(arr[s.charAt(i)]!=0 && c < arr[s.charAt(i)]) c = arr[s.charAt(i)];
                arr[s.charAt(i)] = i+1;
                ans = Math.max(ans, i - c+1);
            }
            return ans;
        }

    public int lengthOfLongestSubstring2(String s) {
        if (s == null) {
            return 0;
        }
        if (s.length() < 2) {
            return s.length();
        }
        char[] chars = s.toCharArray();
        int left = 0, right = 0;
        HashSet<Character> cacheSet = new HashSet<>();
        int maxLength = 0;
        while(right < chars.length){
            if(!cacheSet.contains(chars[right])){
                cacheSet.add(chars[right++]);
            }else{
                maxLength = Math.max(maxLength,right - left);
                cacheSet.remove(chars[left++]);
            }
        }
        maxLength = Math.max(maxLength,right - left);
        return maxLength;
    }

    public int lengthOfLongestSubstring1(String s) {
        if (s == null) {
            return 0;
        }
        if (s.length() < 2) {
            return s.length();
        }
        char[] chars = s.toCharArray();
        HashSet<Character> cacheSet = new HashSet<>();
        int maxLength = 0;
        for (int i = 0; i < chars.length; i++) {
            int currentLength = 0;
            if(chars.length - i <= maxLength){
                return maxLength;
            }
            for (int j = i; j < chars.length; j++) {
                if (!cacheSet.contains(chars[j])) {
                    currentLength++;
                    cacheSet.add(chars[j]);
                } else {
                    cacheSet.clear();
                    break;
                }
            }
            maxLength = Math.max(maxLength, currentLength);
        }
        return maxLength;
    }
}
//leetcode submit region end(Prohibit modification and deletion)
