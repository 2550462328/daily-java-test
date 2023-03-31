package cn.zhanghui.demo.daily.base.collection.array;

/**
 * @author ZhangHui
 * @version 1.0
 * @className TwoArrayMiddleNumber
 * @description 给定两个大小为 m 和 n 的正序（从小到大）数组 nums1 和 nums2。
 * <p>
 * 请你找出这两个正序数组的中位数，并且要求算法的时间复杂度为 O(log(m + n))。
 * <p>
 * 你可以假设 nums1 和 nums2 不会同时为空。
 * <p>
 * nums1 = [1, 3]
 * nums2 = [2]
 * <p>
 * 则中位数是 2.0
 * <p>
 * nums1 = [1, 2]
 * nums2 = [3, 4]
 * <p>
 * 则中位数是 (2 + 3)/2 = 2.5
 * @date 2020/8/19
 */
public class TwoArrayMiddleNumber {

    public static void main(String[] args) {

        TwoArrayMiddleNumber twoArrayMiddleNumbe = new TwoArrayMiddleNumber();

        int[] arr1 = {1, 3, 5, 7};
        int[] arr2 = {2, 4, 6, 8};
        System.out.println(twoArrayMiddleNumbe.getMiddleNumber(arr1, arr2));
        System.out.println(twoArrayMiddleNumbe.getMiddleNumber_advanced(arr1, arr2));
    }

    /**
     * 使用归并排序的方法求解，最后求得中位数
     * 时间复杂度是o(m+n) 空间复杂度是o(m+n)
     *
     * @param arr1
     * @param arr2
     * @return double
     */
    public double getMiddleNumber(int[] arr1, int[] arr2) {

        int aLen1 = arr1.length, aLen2 = arr2.length, tempLen = aLen1 + aLen2;

        int[] tempList = new int[tempLen];

        int tempIndex = 0, i = 0, j = 0;

        while (i < aLen1 && j < aLen2) {
            if (arr1[i] < arr2[j]) {
                tempList[tempIndex++] = arr1[i++];
            } else {
                tempList[tempIndex++] = arr2[j++];
            }
        }

        while (i < aLen1) {
            tempList[tempIndex++] = arr1[i++];
        }

        while (j < aLen2) {
            tempList[tempIndex++] = arr2[j++];
        }

        int middle = tempLen / 2;

        if (tempLen % 2 == 0) {
            return Double.valueOf(tempList[middle - 1] + tempList[middle]) / 2;
        } else {
            return Double.valueOf(tempList[middle]);
        }
    }

    /**
     *  这里我们需要定义一个函数来在两个有序数组中找到第K个元素，下面重点来看如何实现找到第K个元素。
     * 首先，为了避免产生新的数组从而增加时间复杂度，我们使用两个变量i和j分别来标记数组nums1和nums2的起始位置。
     * 然后来处理一些边界问题，比如当某一个数组的起始位置大于等于其数组长度时，说明其所有数字均已经被淘汰了，相当于一个空数组了，那么实际上就变成了在另一个数组中找数字，直接就可以找出来了。
     * 还有就是如果K=1的话，那么我们只要比较nums1和nums2的起始位置i和j上的数字就可以了。
     * 难点就在于一般的情况怎么处理？因为我们需要在两个有序数组中找到第K个元素，为了加快搜索的速度，我们要使用二分法，对K二分，意思是我们需要分别在nums1和nums2中查找第K/2个元素，
     * 注意这里由于两个数组的长度不定，所以有可能某个数组没有第K/2个数字，所以我们需要先检查一下，数组中到底存不存在第K/2个数字，如果存在就取出来，否则就赋值上一个整型最大值。
     * 如果某个数组没有第K/2个数字，那么我们就淘汰另一个数字的前K/2个数字即可。有没有可能两个数组都不存在第K/2个数字呢，这道题里是不可能的，因为我们的K不是任意给的，而是给的m+n的中间值，所以必定至少会有一个数组是存在第K/2个数字的。
     * 最后就是二分法的核心啦，比较这两个数组的第K/2小的数字midVal1和midVal2的大小，如果第一个数组的第K/2个数字小的话，那么说明我们要找的数字肯定不在nums1中的前K/2个数字，所以我们可以将其淘汰，将nums1的起始位置向后移动K/2个，并且此时的K也自减去K/2，调用递归。反之，我们淘汰nums2中的前K/2个数字，并将nums2的起始位置向后移动K/2个，并且此时的K也自减去K/2，调用递归即可。
     */
    public double getMiddleNumber_advanced(int[] nums1, int[] nums2) {
        int m = nums1.length;
        int n = nums2.length;

        // 由于两个数组长度之和 m+n 的奇偶不确定，因此需要分情况来讨论，对于奇数的情况，直接找到最中间的数即可，偶数的话需要求最中间两个数的平均值。
        // 为了简化代码，不分情况讨论，我们使用一个小trick，我们分别找第 (m+n+1) / 2 个，和 (m+n+2) / 2 个，然后求其平均值即可，这对奇偶数均适用。
        // 假如 m+n 为奇数的话，那么其实 (m+n+1) / 2 和 (m+n+2) / 2 的值相等，相当于两个相同的数字相加再除以2，还是其本身。
        int left = (m + n + 1) / 2;
        int right = (m + n + 2) / 2;

        return (findKth(nums1, 0, nums2, 0, left) + findKth(nums1, 0, nums2, 0, right)) / 2.0;

    }

    /**
     * 对于这种解法 我的思考就是滑轮理论
     * 想象一下在nums1和nums2上有两个滑轮 滑轮每次滑动的距离是 k/2 （滑动距离是递减的，类似于缓冲，比如下一次滑动距离就是（k-k/2）/2），当然这个滑动距离可以是1，也就是滑一下比较一下，滑k/2是为了使用二分法将算法复杂度变成O(log(m+n)),滑1那复杂度不就是传统解法O(m+n)了
     * k是什么，k是两个滑轮总共可以滑动的总距离，假设s1是nums1上滑动的距离，s2是num2上滑动的距离，s1 + s2 = k
     * 那滑动方式是怎么判定的呢？ 因为数组是从小到大排列的，在k处的值肯定是大于前面的值，所以滑动方式是谁小谁滑
     * 开始：滑轮1滑动k/2和滑轮2滑动k/2，谁小谁再滑(k-k/2)/2
     * 结束：直到某一个滑轮滑出边界了，或者某一个滑轮滑到了距离k
     *
     * @param nums1 数组1
     * @param i     数组1的起始位置
     * @param nums2 数组2
     * @param j     数组2的起始位置
     * @param k     距离目标中位数的距离
     * @return int
     */
    private int findKth(int[] nums1, int i, int[] nums2, int j, int k) {
        // nums1是空数组，或者滑轮1滑出边界了
        if (i >= nums1.length) return nums2[j + k - 1];

        // nums2是空数组，或者滑轮2滑出边界了
        if (j >= nums2.length) return nums1[i + k - 1];

        // 某一个滑轮滑到了距离k
        if (k == 1) {
            return Math.min(nums1[i], nums2[j]);
        }

        // 比较滑轮1滑动k/2和滑轮2滑动k/2的距离
        int midVal1 = (i + k / 2 - 1 < nums1.length) ? nums1[i + k / 2 - 1] : Integer.MAX_VALUE;
        int midVal2 = (j + k / 2 - 1 < nums2.length) ? nums2[j + k / 2 - 1] : Integer.MAX_VALUE;

        // 谁小谁再滑动(k-k/2)/2
        if (midVal1 < midVal2) {
            return findKth(nums1, i + k / 2, nums2, j, k - k / 2);
        } else {
            return findKth(nums1, i, nums2, j + k / 2, k - k / 2);
        }
    }
}
