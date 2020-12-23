package cn.zhanghui.demo.daily.base.algorithm.sort.base;

import java.util.Arrays;

/**
 * 选择排序
 * 　　a、将第一个值看成最小值
 * 　　b、然后和后续的比较找出最小值和下标
 * 　　c、交换本次遍历的起始值和最小值
 * 　　d、说明：每次遍历的时候，将前面找出的最小值，看成一个有序的列表，后面的看成无序的列表，然后每次遍历无序列表找出最小值
 * @author: ZhangHui
 * @date: 2020/12/5 14:46
 * @version：1.0
 */
public class SelectSort {

    public static void main(String[] args) {

        int[] nums = {2, 1, 4, 3, 4, 1, 5, 2};
        new SelectSort().sort(nums);
        System.out.println(Arrays.toString(nums));
    }

    public void sort(int[] nums) {

        for (int i = 0; i < nums.length; i++) {

            int min = nums[i];
            int min_index = i;

            for (int j = i + 1; j < nums.length; j++) {
                if(nums[j] < min){
                    min = nums[j];
                    min_index = j;
                }
            }

            int temp = nums[i];
            nums[i] = min;
            nums[min_index] = temp;
        }
    }
}
