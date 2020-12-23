package cn.zhanghui.demo.daily.base.algorithm.sort.base;

import java.util.Arrays;

/**
 * 归并排序
 *     a、将列表按照对等的方式进行拆分
 * 　　b、拆分小最小快的时候，在将最小块按照原来的拆分，进行合并
 * 　　c、合并的时候，通过左右两块的左边开始比较大小。小的数据放入新的块中
 * 　　d、说明：简单一点就是先对半拆成最小单位，然后将两半数据合并成一个有序的列表。
 * @author: ZhangHui
 * @date: 2020/12/5 14:46
 * @version：1.0
 */
public class MergeSort {

    public static void main(String[] args) {

        int[] nums = {2, 1, 4, 3, 4, 1, 5, 2};
        new MergeSort().sort(nums);
        System.out.println(Arrays.toString(nums));
    }

    public void sort(int[] nums) {

        int start = 0;
        int end = nums.length - 1;
        mergeSort(start, end, nums);
    }

    private void mergeSort(int start, int end, int[] nums) {

        if (end > start) {

            mergeSort(start, (start + end) / 2, nums);
            mergeSort((start + end) / 2 + 1, end, nums);

            int[] result = new int[end - start + 1];
            int left = start;
            int right = (start + end) / 2 + 1;
            int i = 0;
            while (left <= (start + end) / 2 && right <= end) {
                if (nums[left] < nums[right]) {
                    result[i++] = nums[left];
                    left++;
                } else {
                    result[i++] = nums[right];
                    right++;
                }
            }

            while (left <= (start + end) / 2 || right <= end) {

                if (left <= (start + end) / 2) {
                    result[i++] = nums[left];
                    left++;
                } else {
                    result[i++] = nums[right];
                    right++;
                }
            }

            for (int j = start; j <= end; j++) {
                nums[j] = result[j - start];
            }
        }
    }

}
