package cn.zhanghui.demo.daily.base.algorithm.sort.line_sort.base_sort;

import java.util.Arrays;

/**
 * @ClassName: BaseCount.java
 * @Description: 这是一个简单的基数排序
 * @author: ZhangHui
 * @date: 2019年11月12日 下午2:40:23
 */
public class BaseCount {
    // ascii码的取值范围
    private static final int ASSII_RANGE = 128;

    public static String[] radixSort(String[] array, int maxLength) {
        // 排序结果数组，用于存储每一次按位排序的临时结果
        String sortedArray[] = new String[array.length];
        // 从个位开始比较到最高位
        for (int k = maxLength - 1; k >= 0; k--) {
            // 1、创建辅助排序的统计数组，并将待排序的字符对号入座
            // 这里将ascii码的范围作为数组的长度
            int[] count = new int[ASSII_RANGE];
            for (int i = 0; i < array.length; i++) {
                int index = getCharIndex(array[i], k);
                count[index]++;
            }
            //2、统计数组做变形，后面的元素等于前面的元素之和
            for (int i = 1; i < count.length; i++) {
                count[i] = count[i] + count[i - 1];
            }

            //3、倒序遍历原始数列，从统计数组找到正确位置然后输出到结果数组
            for (int i = array.length - 1; i >= 0; i--) {
                int index = getCharIndex(array[i], k);
                int sortedIndex = count[index] - 1;
                sortedArray[sortedIndex] = array[i];
                count[index]--;
            }
            //4、下一轮排序以上一轮的结果为基础
            array = sortedArray.clone();
        }
        return array;
    }

    // 返回字符串第k位所对应的ascii码序号
    private static int getCharIndex(String str, int k) {
        // 如果str的k位不存在，直接返回0，相当于在不存在的位置补0
        if (str.length() < k + 1) {
            return 0;
        }
        return str.charAt(k);
    }

    public static void main(String[] args) {
        String array[] = {"ad", "dwe", "ab", "csa", "da", "1a", "$a"};
        System.out.println(Arrays.toString(radixSort(array, 3)));
    }
}
