package cn.zhanghui.demo.daily.base.algorithm.sort.line_sort.count_sort;

import cn.zhanghui.demo.daily.base.algorithm.sort.base.AbstractBaseSort;

import java.util.Arrays;

/**
 * @ClassName: CountSort1.java
 * @Description: 这是一个简单的计数排序，实现0-20之间数字的排序
 * 计数排序的算法复杂度是o(n+m)  n是排序数组长度 m是统计数组长度 空间复杂度是o(m)
 * 计数排序不适用的场景
 * 1、当数列最大最小值差距过大时
 * 2、当数列元素不是整数
 * @author: ZhangHui
 * @date: 2019年11月5日 上午8:53:44
 */
public class SimpleCountSort extends AbstractBaseSort {

    public static void main(String[] args) {
        new SimpleCountSort().baseSort(true);
    }

    @Override
    public void sort(int[] source, boolean asc) {
        // 使用max - min + 1的值作为container的长度
        int max = source[0], min = source[0];
        for (int i = 1; i < source.length; i++) {
            if (source[i] > max) {
                max = source[i];
            } else if (source[i] < min) {
                min = source[i];
            }
        }

        // 将待排序数组的值依次对应container的下标
        int[] container = new int[max - min + 1];

        for (int i = 0; i < source.length; i++) {
            container[source[i] - min]++;
        }
        // 从头遍历container，输出到arraySorted中即是排序
        int[] arraySorted = new int[source.length];
        int k = 0;

        for (int i = 0; i < container.length; i++) {
            for (int j = 0; j < container[i]; j++) {
                arraySorted[k++] = i + min;
            }
        }
        System.arraycopy(arraySorted, 0, source, 0, arraySorted.length);
    }
}
