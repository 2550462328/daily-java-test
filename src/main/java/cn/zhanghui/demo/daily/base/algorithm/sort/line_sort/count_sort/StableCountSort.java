package cn.zhanghui.demo.daily.base.algorithm.sort.line_sort.count_sort;

import cn.zhanghui.demo.daily.base.algorithm.sort.base.AbstractBaseSort;

/**
 * Description:
 * 这是一个计数排序的稳定排序算法，记住放入的顺序，在数值一样时根据放入顺序排序
 * 计数排序的算法复杂度是o(n+m)  n是排序数组长度 m是统计数组长度 空间复杂度是o(m)
 *
 * @author createdBy huizhang43.
 * @date createdAt 2023/4/4 9:24
 **/
public class StableCountSort extends AbstractBaseSort {

    public static void main(String[] args) {

        new StableCountSort().baseSort(true);
    }

    @Override
    public void sort(int[] source, boolean asc) {
        int min = source[0];
        int max = source[0];
        for (int i = 1; i < source.length; i++) {
            if (source[i] < min) {
                min = source[i];
            }
            if (source[i] > max) {
                max = source[i];
            }
        }
        if (min == max) {
            return;
        }
        int[] countArray = new int[max - min + 1];
        for (int i = 0; i < source.length; i++) {
            countArray[source[i] - min]++;
        }

        // 关键一：将后面的元素 = 自身值 + 前面的元素之和
        // 这样的目的是为了 让统计数组存储的元素值，等于相应整数的最终排序位置
        // 可以理解成当有重复数据的时候，先放入的放在下面，遍历的时候，放在下面的就排位靠前
        for (int i = 1; i < countArray.length; i++) {
            countArray[i] += countArray[i - 1];
        }

        int[] sortedArray = new int[source.length];
        // 关键二：倒序遍历原始数列，从统计数组找到正确位置，输出到结果数组
        for (int i = source.length - 1; i >= 0; i--) {
            // countArray[source[i] - min] 表示当前元素在最终排列数组中的位置
            sortedArray[countArray[source[i] - min] - 1] = source[i];
            countArray[source[i] - min]--;
        }
        System.arraycopy(sortedArray, 0, source, 0, sortedArray.length);
    }
}
