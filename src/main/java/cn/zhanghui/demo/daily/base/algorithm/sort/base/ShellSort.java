package cn.zhanghui.demo.daily.base.algorithm.sort.base;

import java.util.Arrays;

/**
 * 希尔排序
 * 　  a、基本上和插入排序一样的道理
 * 　　b、不一样的地方在于，每次循环的步长，通过减半的方式来实现
 * 　　c、说明：基本原理和插入排序类似，不一样的地方在于。通过间隔多个数据来进行插入排序。
 * @author: ZhangHui
 * @date: 2020/12/5 14:46
 * @version：1.0
 */
public class ShellSort {

    public static void main(String[] args) {

        int[] nums = {2, 1, 4, 3, 4, 1, 5, 2};
        new ShellSort().sort(nums);
        System.out.println(Arrays.toString(nums));
    }

    public void sort(int[] nums) {

        // i控制间隔，最小是1，等同于插入排序
        for (int i = nums.length / 2; i > 0; i /= 2) {
           // j控制每次比较的起点
            for (int j = i; j < nums.length; j++) {
                // k控制最小值的传递
                for (int k = j; k > 0 && k - i >= 0; k -= i) {
                    if (nums[k] < nums[k - i]) {
                        int temp = nums[k];
                        nums[k] = nums[k - i];
                        nums[k - i] = temp;
                    } else {
                        break;
                    }
                }
            }
        }
    }
}
