package cn.zhanghui.demo.daily.base.algorithm.sort.base;

import java.util.Arrays;

/**
 * 冒泡排序
 * 　　a、冒泡排序，是通过每一次遍历获取最大/最小值
 * 　　b、将最大值/最小值放在尾部/头部
 * 　　c、然后除开最大值/最小值，剩下的数据在进行遍历获取最大/最小值
 * @author: ZhangHui
 * @date: 2020/12/5 14:46
 * @version：1.0
 */
public class BubbleSort {

    public static void main(String[] args) {

        int[] nums = {2,1,4,3,4,1,5,2};
        new BubbleSort().sort(nums);
        System.out.println(Arrays.toString(nums));
    }

    public void sort(int[] nums){

        for(int i = 0; i < nums.length; i++){

            for(int j = 0; j < nums.length - i - 1; j++){
                if(nums[j] > nums[j+1]){
                    int temp = nums[j];
                    nums[j] = nums[j+1];
                    nums[j+1] = temp;
                }
            }
        }
    }
}
