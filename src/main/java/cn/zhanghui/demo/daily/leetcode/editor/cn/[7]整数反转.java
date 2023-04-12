//给你一个 32 位的有符号整数 x ，返回将 x 中的数字部分反转后的结果。 
//
// 如果反转后整数超过 32 位的有符号整数的范围 [−2³¹, 231 − 1] ，就返回 0。 
//假设环境不允许存储 64 位整数（有符号或无符号）。
//
// 
//
// 示例 1： 
//
// 
//输入：x = 123
//输出：321
// 
//
// 示例 2： 
//
// 
//输入：x = -123
//输出：-321
// 
//
// 示例 3： 
//
// 
//输入：x = 120
//输出：21
// 
//
// 示例 4： 
//
// 
//输入：x = 0
//输出：0
// 
//
// 
//
// 提示： 
//
// 
// -2³¹ <= x <= 2³¹ - 1 
// 
//
// Related Topics 数学 👍 3809 👎 0

package cn.zhanghui.demo.daily.leetcode.editor.cn;
//leetcode submit region begin(Prohibit modification and deletion)
class Solution7 {

    public int reverse(int x) {
        int res = 0;
        while (x != 0) {
            int pop = x % 10;
            x /= 10;
            // 比较不一定要获取完整的值后再比较
            if (res > Integer.MAX_VALUE/10 || (res == Integer.MAX_VALUE/10 && pop > 7))
                return 0;
            if (res < Integer.MIN_VALUE/10 || (res == Integer.MIN_VALUE/10 && pop < -8))
                return 0;
            res = res * 10 + pop;
        }
        return res;
    }

    public int reverse1(int x) {
        char[] s = String.valueOf(x).toCharArray();
        if(s.length < 2){
            return x;
        }
        int i = s[0] == '-' ? 1 : 0;
        int j = s.length - 1;

        while (s[j] == '0') {
            j--;
        }

        int m = i, n = j;
        while (m < n) {
            char temp = s[m];
            s[m] = s[n];
            s[n] = temp;

            m++;
            n--;
        }

        int result = 0;
        try {
            result = Integer.valueOf(new String(s).substring(0,j + 1));
        } catch (NumberFormatException e) {
            return 0;
        }
        return result;
    }

    public int reverse2(int x) {
        if(x < 10 && x > -10){
            return x;
        }
        int negative = x < 0 ? -1 : 1;
        String s = String.valueOf(x);
        char[] chars = s.toCharArray();
        StringBuilder sb = new StringBuilder();
        char[] cp = String.valueOf(Integer.MAX_VALUE).toCharArray();
        boolean notCp = negative < 0 ? cp.length > chars.length - 1 : cp.length > chars.length;
        boolean overLimit = false;
        int border = negative < 0 ? 1 : 0;
        for (int i = chars.length - 1; i >= border; i--) {
            if (sb.length() != 0 || chars[i] != '0') {
                if (!notCp && !overLimit) {
                    if(i == border && negative < 0){
                        if(chars[i] > cp[chars.length - i - 1] +1){
                            overLimit = true;
                        }
                    }else if(chars[i] > cp[chars.length - i - 1]) {
                        overLimit = true;
                    }else if(chars[i] < cp[chars.length - i - 1]){
                        notCp = true;
                    }
                }
                sb.append(chars[i]);
            } else {
                notCp = true;
            }
        }

        if (overLimit) {
            return 0;
        }
        return negative * Integer.parseInt(sb.toString());
    }
}
//leetcode submit region end(Prohibit modification and deletion)
