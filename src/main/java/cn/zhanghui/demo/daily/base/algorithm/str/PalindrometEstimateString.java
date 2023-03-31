package cn.zhanghui.demo.daily.base.algorithm.str;

/**
 * @author ZhangHui
 * @version 1.0
 * @className EstimatePalindrometString
 * @description 给定一个字符串，验证它是否是回文串，只考虑字母和数字字符，可以忽略字母的大小写。
 * <p>
 * 说明：本题中，我们将空字符串定义为有效的回文串。
 * <p>
 * 示例 1:
 * 输入: "A man, a plan, a canal: Panama"
 * 输出: true
 * @date 2020/9/11
 */
public class PalindrometEstimateString {

    public static void main(String[] args) {
//        PalindrometEstimateString palindrometEstimateString = new PalindrometEstimateString();
//
//        String s = "A man, a plan, a canal: Panama";
//
//        System.out.println(palindrometEstimateString.isPalindrome(s));

        char[] chars = new char[256];
//        digits 48~56
//        letter 96~121
        System.out.println('z' - 1);

    }

    /**
     * 采用双指针解决
     */
    public boolean isPalindrome(String s) {

        if (s.length() < 2) {
            return true;
        }

        s = s.toLowerCase();

        int i = 0, j = s.length() - 1;

        while (i < j) {

            char s_i = s.charAt(i);
            char s_j = s.charAt(j);

            if (!Character.isLetterOrDigit(s_i)) {
                i++;
                continue;
            }

            if (!Character.isLetterOrDigit(s_j)) {
                j--;
                continue;
            }

            if (s_i != s_j) {
                return false;
            }
            i++;
            j--;
        }

        return true;
    }
}
