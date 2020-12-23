package cn.zhanghui.demo.daily.base.collection.array;

import java.util.Arrays;

/**
 * @author ZhangHui
 * @version 1.0
 * @className RecycleArrayTest
 * @description 给定一个数组，将数组中的元素向右移动 k 个位置，其中 k 是非负数。
 * <p>
 * 示例 1:
 * <p>
 * 输入: [1,2,3,4,5,6,7] 和 k = 3
 * 输出: [5,6,7,1,2,3,4]
 * 解释:
 * [1,2,3,4,5,6,7]
 * 向右旋转 3 步: [5,6,7,1,2,3,4]
 * <p>
 * 要求使用空间复杂度为 O(1) 的 原地 算法。
 * @date 2020/8/31
 */
public class RecycleArrayTest {

    /**
     * 一个单位一个单位的往右挪
     * 需要两个临时值
     * 空间复杂度O(1)；时间复杂度O(n*k)
     */
    public void rotateStepByStep(int[] nums, int k) {
        int len = nums.length;

        if (len < 2) {
            return;
        }

        int temp1, temp2;

        for (int i = 0; i < k; i++) {
            temp2 = nums[len - 1];
            for (int j = 0; j < len; j++) {
                temp1 = nums[j];
                nums[j] = temp2;
                temp2 = temp1;
            }
        }
    }

    /**
     * 直接移动k步，在num[i+k]的地方继续移动k步，直到数组中所有元素都移动了K步
     * 需要注意的是 有可能出现从nums[0]移动k步之后最终又回到了nums[0]
     *
     * 如果K > n ,则相当于只移动了 K % n步
     * 空间复杂度O(1)；时间复杂度O(n)
     */
    public void rotateStepToK(int[] nums, int k) {
        k = k % nums.length;
        int count = 0;
        for (int start = 0; count < nums.length; start++) {
            int current = start;
            int prev = nums[start];
            do {
                int next = (current + k) % nums.length;
                int temp = nums[next];
                nums[next] = prev;
                prev = temp;
                current = next;
                count++;
            } while (start != current);
        }
    }

    /**
     * 使用一个只读数组来复制原数组，用于计算值的时候可以用到原数组的元素
     * 空间复杂度O(n)，时间复杂度O(n)
     */
    public void rotateArrayCopy(int[] nums, int k) {

        int len = nums.length;

        if (len < 2) {
            return;
        }

        int[] numsReadonly = Arrays.copyOf(nums, len);

        for (int i = 0; i < len; i++) {
            nums[(i + k) % len] = numsReadonly[i];
        }
    }

    /**
     * 这个方法基于这个事实：当我们旋转数组 k 次， k%nk\%nk%n 个尾部元素会被移动到头部，剩下的元素会被向后移动。
     * 在这个方法中，我们首先将所有元素反转。然后反转前 k 个元素，再反转后面 n−kn-kn−k 个元素，就能得到想要的结果。
     * <p>
     * 假设 n=7n=7n=7 且 k=3k=3k=3 。
     * 原始数组                  : 1 2 3 4 5 6 7
     * 反转所有数字后             : 7 6 5 4 3 2 1
     * 反转前 k 个数字后          : 5 6 7 4 3 2 1
     * 反转后 n-k 个数字后        : 5 6 7 1 2 3 4 --> 结果
     * <p>
     * 空间复杂度O(1)，时间复杂度O(n)
     */
    public void rotateReverse(int[] nums, int k) {
        k %= nums.length;
        reverse(nums, 0, nums.length - 1);
        reverse(nums, 0, k - 1);
        reverse(nums, k, nums.length - 1);
    }

    private void reverse(int[] nums, int start, int end) {
        while (start < end) {
            int temp = nums[start];
            nums[start] = nums[end];
            nums[end] = temp;
            start++;
            end--;
        }
    }

}
