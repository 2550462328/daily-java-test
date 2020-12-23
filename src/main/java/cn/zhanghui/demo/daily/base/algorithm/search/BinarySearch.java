package cn.zhanghui.demo.daily.base.algorithm.search;

/**
 * 二分查找法
 *
 * @author: ZhangHui
 * @date: 2020/12/5 17:29
 * @version：1.0
 */
public class BinarySearch {

    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4, 5, 6, 7};
        System.out.println(new BinarySearch().searchWithNoRecursion(7, nums));
    }

    private int searchWithRecursion(int target, int start, int end, int[] nums) {
        if (start > end) {
            return -1;
        }
        int mid = (start + end) / 2;

        if (nums[mid] > target) {
            return searchWithRecursion(target, start, mid, nums);
        } else if (nums[mid] < target) {
            return searchWithRecursion(target, mid + 1, end, nums);
        } else {
            return mid;
        }
    }

    private int searchWithNoRecursion(int target, int[] nums) {

        int left = 0, right = nums.length - 1;

        int mid = (left + right) / 2;

        while (left <= right) {

            if (nums[mid] > target) {
                right = mid;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                return mid;
            }
            mid = (left + right) / 2;
        }
        return -1;
    }
}
