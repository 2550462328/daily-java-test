package cn.zhanghui.demo.daily.base.algorithm.sort.base;

import java.util.Arrays;

/**
 * 插入排序
 *  　　a、默认从第二个数据开始比较。
 * 　　 b、如果第二个数据比第一个小，则交换。然后在用第三个数据比较，如果比前面小，则插入（狡猾）。否则，退出循环
 * 　　 c、说明：默认将第一数据看成有序列表，后面无序的列表循环每一个数据，如果比前面的数据小则插入（交换）。否则退出。
 * @author: ZhangHui
 * @date: 2020/12/5 14:46
 * @version：1.0
 */
public class InsertSort {

    public static void main(String[] args) {

        int[] nums = {2, 1, 4, 3, 4, 1, 5, 2};
        new InsertSort().sort(nums);
        System.out.println(Arrays.toString(nums));
    }

    public void sort(int[] nums) {

        for (int i = 1; i < nums.length; i++) {

            for (int j = i; j > 0; j--) {

                if(nums[j] < nums[j-1]){
                    int temp = nums[j];
                    nums[j] = nums[j-1];
                    nums[j-1] = temp;
                }else{
                    break;
                }
            }
        }
    }
}
