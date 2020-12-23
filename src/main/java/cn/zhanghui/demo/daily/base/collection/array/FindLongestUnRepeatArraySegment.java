package cn.zhanghui.demo.daily.base.collection.array;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 给定一个字符串，请你找出其中不含有重复字符的 最长子串 的长度。
 *
 * 示例 1:
 *
 * 输入: "abcabcbb"
 * 输出: 3
 * 解释: 因为无重复字符的最长子串是 "abc"，所以其长度为 3。
 *
 * @author: ZhangHui
 * @date: 2020/11/13 9:00
 * @version：1.0
 */
public class FindLongestUnRepeatArraySegment {

    public static void main(String[] args) {
        String s = "pwwkew";
        System.out.println(new FindLongestUnRepeatArraySegment().lengthOfLongestSubstring_advanced(s));
    }

    /**
     * 双指针加哈希 暴力求解
     */
    public int lengthOfLongestSubstring(String s) {
        int left = 0, right = 0;

        char[] chars = s.toCharArray();

        Map<Character,Integer> map = null;

        int maxLength = 0;

        while(right < chars.length){

            if(map == null){
                map = new HashMap<>();

                // 存放未重复的元素
                for(int i = left; i < right ; i++){
                    map.put(chars[i],i);
                }
            }

            if(!map.containsKey(chars[right])){
                map.put(chars[right],right);
            }else{
                maxLength = Math.max(maxLength,right-left);
                left = map.get(chars[right]) + 1;
                map = null;
            }
            right++;
        }
        maxLength = Math.max(maxLength,right - left);
        return maxLength;
    }

    /**
     * 双指针加哈希 暴力求解 优化
     * 优化部分：不借助Map存储未重复元素的下标，如果遇到重复情况，从Left开始进行清除，直到不重复为止
     * left和right都走了s.length的距离 复杂度 O(n)
     */
    public int lengthOfLongestSubstring_advanced(String s) {
        int left = 0, right = 0;

        char[] chars = s.toCharArray();

        Set<Character> set = new HashSet<>();

        int maxLength = 0;

        while(right < chars.length){
            if(!set.contains(chars[right])){
                set.add(chars[right]);
                right++;
            }else{
                maxLength = Math.max(maxLength,right-left);
                set.remove(chars[left++]);
            }
        }
        maxLength = Math.max(maxLength,right - left);
        return maxLength;
    }
}
