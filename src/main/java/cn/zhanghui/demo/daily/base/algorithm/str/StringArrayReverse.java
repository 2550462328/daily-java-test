package cn.zhanghui.demo.daily.base.algorithm.str;

import org.apache.commons.lang.StringUtils;

/**
 * @author ZhangHui
 * @version 1.0
 * @className StringArrayReverse
 * @description 编写一个函数，其作用是将输入的字符串反转过来。输入字符串以字符数组 char[] 的形式给出。
 * <p>
 * 不要给另外的数组分配额外的空间，你必须原地修改输入数组、使用 O(1) 的额外空间解决这一问题。
 * @date 2020/9/9
 */
public class StringArrayReverse {

    public static void main(String[] args) {
        char[] s = {'H', 'a', 'n', 'n', 'a', 'h'};

        StringArrayReverse stringArrayReverse = new StringArrayReverse();

        stringArrayReverse.reverseString(s);

        System.out.println(StringUtils.reverse(new String(s)));
    }

    public void reverseString(char[] s) {
        int i = 0, j = s.length - 1;

        while (i < j) {
            char temp = s[i];
            s[i] = s[j];
            s[j] = temp;

            i++;
            j--;
        }
    }

}
