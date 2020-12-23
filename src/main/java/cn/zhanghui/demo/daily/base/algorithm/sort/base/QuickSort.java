package cn.zhanghui.demo.daily.base.algorithm.sort.base;

import java.util.Arrays;

/**
 * 快速排序
 *    a、确认列表第一个数据为中间值，第一个值看成空缺（低指针空缺）。
 * 　　b、然后在剩下的队列中，看成有左右两个指针（高低）。
 * 　　c、开始高指针向左移动，如果遇到小于中间值的数据，则将这个数据赋值到低指针空缺，并且将高指针的数据看成空缺值（高指针空缺）。然后先向右移动一下低指针，并且切换低指针移动。
 * 　　d、当低指针移动到大于中间值的时候，赋值到高指针空缺的地方。然后先高指针向左移动，并且切换高指针移动。重复c、d操作。
 * 　　e、直到高指针和低指针相等时退出，并且将中间值赋值给对应指针位置。
 * 　　f、然后将中间值的左右两边看成行的列表，进行快速排序操作。
 *
 * 快速排序的目的在于，找到一个中间值，中间值左边都比它小，中间值右边都比它大，然后分别在左边和右边做同样的递归操作
 * 至于代码复杂的原因，是因为希望尽可能的原地变换，一开始左边取一个中间值，那么就需要从右边开始找一个值比它小的去替换它，那么右边这个位置就没用了
 * 我再从左边找一个比中间值大的值去替换右边的空缺，这样左边这边就没用了，依此类推
 *
 * @author: ZhangHui
 * @date: 2020/12/5 14:46
 * @version：1.0
 */
public class QuickSort {

    public static void main(String[] args) {

        int[] nums = {2,1,4,3,4,1,5,2};
        new QuickSort().sort(nums);
        System.out.println(Arrays.toString(nums));
    }

    public void sort(int[] nums){

        int low = 0;
        int high = nums.length -1;
        quickSort(low,high,nums);
    }

    private void quickSort(int low, int high,int[] nums){

        // high和low相遇了，就不用再找了
        if(high - low < 1){
            return;
        }

        int midValue = nums[low];

        // 先记录下边界值
        int start = low,end = high;

        // 切换从右边找还是从左边找
        boolean isHighMove = true;

        while(true){
            if(low < high) {
                if (isHighMove) {

                    if (nums[high] > midValue) {
                        high--;
                    } else {
                        nums[low] = nums[high];
                        low++;
                        isHighMove = false;
                    }
                } else {
                    if (nums[low] < midValue) {
                        low++;
                    } else {
                        nums[high] = nums[low];
                        high--;
                        isHighMove = true;
                    }
                }
            }else{
                nums[low] = midValue;
                break;
            }
        }

        // 递归排序
        quickSort(start,low-1,nums);
        quickSort(low+1,end,nums);
    }
}
