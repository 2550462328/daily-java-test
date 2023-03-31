package cn.zhanghui.demo.daily.base.algorithm.str;

/**
 * @author ZhangHui
 * @version 1.0
 * @className EstimateIsAnagram
 * @description 给定两个字符串 s 和 t ，编写一个函数来判断 t 是否是 s 的字母异位词。
 * <p>
 * 示例 1:
 * <p>
 * 输入: s = "anagram", t = "nagaram"
 * 输出: true
 * <p>
 * 你可以假设字符串只包含小写字母。
 * @date 2020/9/10
 */
public class EstimateIsAnagram {

    public static void main(String[] args) {
        EstimateIsAnagram estimateIsAnagram = new EstimateIsAnagram();

        System.out.println(estimateIsAnagram.isAnagram("a", "b"));
    }

    /**
     * 采用char[26]来进行解决
     */
    public boolean isAnagram(String s, String t) {

        if (s.length() != t.length()) {
            return false;
        }

        int[] chars = new int[26];

        for (int i = 0; i < s.length(); i++) {
            chars[s.charAt(i) - 'a']++;
            chars[t.charAt(i) - 'a']--;
        }

        for (int c : chars) {
            if (c != 0) {
                return false;
            }
        }
        return true;

    }
}
