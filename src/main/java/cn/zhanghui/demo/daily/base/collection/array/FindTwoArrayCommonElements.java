package cn.zhanghui.demo.daily.base.collection.array;

import java.util.*;

/**
 * @author ZhangHui
 * @version 1.0
 * @className FindTwoArrayCommonElements
 * @description 给定两个数组，编写一个函数来计算它们的交集。
 * <p>
 * 示例 1：
 * 输入：nums1 = [1,2,2,1], nums2 = [2,2]
 * 输出：[2,2]
 * @date 2020/9/2
 */
public class FindTwoArrayCommonElements {

    public static void main(String[] args) {
        FindTwoArrayCommonElements findTwoArrayCommonElements = new FindTwoArrayCommonElements();

        int[] nums1 = {1, 2, 2, 1};
        int[] nums2 = {2, 2};

        System.out.println(Arrays.toString(findTwoArrayCommonElements.intersectSortAndTwoPointer(nums1, nums2)));
    }

    /**
     * 使用ArrayList去装较短的数组
     * 在另一个数组中遍历如果有重复的话就添加到结果中
     * 性能极差，因为在ArrayList中做contains操作就是一顿全表扫描
     */
    public int[] intersectArrayList(int[] nums1, int[] nums2) {

        int len1 = nums1.length;
        int len2 = nums2.length;

        if (len1 > len2) {
            return intersectArrayList(nums2, nums1);
        }

        if (len1 == 0 || len2 == 0) {
            return new int[0];
        }

        List arrayList = new ArrayList(len1);

        for (int i : nums1) {
            arrayList.add(i);
        }

        int[] result = new int[len1];

        int index = 0;

        for (int i = 0; i < len2; i++) {
            if (arrayList.contains(nums2[i])) {
                result[index++] = nums2[i];
                arrayList.remove((Integer) nums2[i]);
            }
        }
        return Arrays.copyOfRange(result, 0, index);
    }

    /**
     * 使用HashMap去装较短的数组中每个元素出现的次数
     * 在另一个数组中遍历如果有重复元素的话就将次数减一，并将该元素添加到结果
     * 性能较好，毕竟用hash做查询时间复杂度是O(1)
     */
    public int[] intersectHashMap(int[] nums1, int[] nums2) {

        int len1 = nums1.length;
        int len2 = nums2.length;

        if (len1 > len2) {
            return intersectHashMap(nums2, nums1);
        }

        if (len1 == 0 || len2 == 0) {
            return new int[0];
        }

        Map<Integer, Integer> hashMap = new HashMap<>(len1);

        for (int i : nums1) {
            int count = hashMap.getOrDefault(i, 0) + 1;
            hashMap.put(i, count);
        }

        int[] result = new int[len1];

        int index = 0;

        for (int i = 0; i < len2; i++) {
            int count = hashMap.getOrDefault(nums2[i], 0);
            if (count > 0) {
                result[index++] = nums2[i];
                hashMap.put(nums2[i], --count);
            }
        }
        return Arrays.copyOfRange(result, 0, index);
    }

    /**
     * 效率最高，没有添加额外的容器
     * 对两数组进行排序
     * 使用双指针法，依次进行比较，值较小的指针往后移动，值相等的情况下两指针同时移动并且将此时的值记录到结果中
     * 所以说对数组问题操作 优先考虑双指针  > 借助哈希操作 > 暴力求解
     * 当然可能会有一些骚操作，比如用位运算，将数组反转求解，直接将数组对半求解等等逆天的骚操作，效率可不就更高了
     */
    public int[] intersectSortAndTwoPointer(int[] nums1, int[] nums2) {

        int len1 = nums1.length;
        int len2 = nums2.length;

        if (len1 == 0 || len2 == 0) {
            return new int[0];
        }

        if (len1 > len2) {
            return intersectSortAndTwoPointer(nums2, nums1);
        }

        int[] result = new int[len1];
        int index = 0;

        Arrays.sort(nums1);
        Arrays.sort(nums2);

        int p1 = 0, p2 = 0;

        while (p1 < len1 && p2 < len2) {
            if (nums1[p1] > nums2[p2]) {
                p2++;
            } else if (nums1[p1] < nums2[p2]) {
                p1++;
            } else {
                result[index++] = nums1[p1];
                p1++;
                p2++;
            }
        }

        return Arrays.copyOfRange(result, 0, index);
    }
}
