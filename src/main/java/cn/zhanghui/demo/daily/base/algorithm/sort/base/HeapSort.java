package cn.zhanghui.demo.daily.base.algorithm.sort.base;

import java.util.Arrays;

/**
 * 堆排序
 * 　　a、先根据当前数组构建大顶堆
 * 　　b、将大顶堆的最大值，也就是nums的首位，放到末位
 *     c、不断重复，就实现从小到大的排列
 *
 * @author: ZhangHui
 * @date: 2020/12/5 14:46
 * @version：1.0
 */
public class HeapSort {

    public static void main(String[] args) {

        int[] nums = {2, 1, 4, 3, 4, 1, 5, 2};
        new HeapSort().sort(nums);
        System.out.println(Arrays.toString(nums));
    }

    public void sort(int[] nums) {
        // 根据nums数组构建大顶堆
        for(int i = nums.length / 2 -1; i >= 0; i--){
            heapAdjust(nums,i,nums.length);
        }

        for(int i = nums.length - 1; i > 0; i--){
            // 交换大顶堆的最大值和最小值
            // 也就是将nums数组的最后一个和第一个交换，依次减小i，就会不断的将大顶堆的最大值放在后面
            int temp = nums[i];
            nums[i] = nums[0];
            nums[0] = temp;

            // 交换完后再调整大顶堆
            // 最大值被放到后面了，所以，后面的部分我们不希望再调整，所以限制leftChild的范围
            heapAdjust(nums,0,i);
        }
    }

    private void heapAdjust(int[] nums, int root, int len) {
        int temp = nums[root];
        // root节点的左子节点
        int leftChild = 2 * root + 1;

        while (leftChild < len) {

            // 取左右子节点较大一个
            if(leftChild + 1 < len && nums[leftChild + 1] > nums[leftChild]){
                leftChild++;
            }

            // 当前根节点大于较大的子节点，则不用交换，当前大顶堆是正确的
            if(temp >= nums[leftChild]){
                break;
            }

            // 替换根节点
            nums[root] = nums[leftChild];
            root = leftChild;

            // 再拿原先的根节点和原先根节点的较大子节点的左子节点比较
            leftChild = 2 * root + 1;
        }

        // 现在我们的根节点成功找到了它的位置，给它赋值
        nums[root] = temp;
    }
}
