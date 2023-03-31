package cn.zhanghui.demo.daily.base.algorithm.sort.base;

import java.util.Arrays;

/**
 * Description:
 * 堆排序
 * 　　a、先根据当前数组构建大顶堆
 * 　　b、将大顶堆的最大值，也就是nums的首位，放到末位
 * c、不断重复，就实现从小到大的排列
 *
 * @author createdBy huizhang43.
 * @date createdAt 2023/3/28 10:43
 **/
public class HeapSort extends AbstractBaseSort {

    public static void main(String[] args) {
        new HeapSort().baseSort(true);
    }

    @Override
    public void sort(int[] source, boolean asc) {
        // 按需求构建最大堆 / 最小堆
        for (int i = source.length / 2 - 1; i >= 0; i--) {
            heapAdjust(source, i, source.length);
            System.out.println();
        }

        for (int i = source.length - 1; i > 0; i--) {
            // 交换大顶堆的最大值和最小值
            // 也就是将nums数组的最后一个和第一个交换，依次减小i，就会不断的将大顶堆的最大值放在后面
            int temp = source[0];
            source[0] = source[i];
            source[i] = temp;

            // 交换完后再调整大顶堆
            // 最大值被放到后面了，所以，后面的部分我们不希望再调整，所以限制leftChild的范围
            heapAdjust(source, 0, i);
        }
    }

    /**
     * 调整堆
     * 不断将root节点下沉，放到一个合适的地方（1. 无叶子节点 2. 比叶子节点大（小））
     *
     * @param nums   操作数组
     * @param root   待下沉节点
     * @param length 下沉的边界值
     */
    private void heapAdjust(int[] nums, int root, int length) {
        System.out.printf("调整堆数据前：%s", Arrays.toString(nums));
        int temp = nums[root];
        int leftChild = root * 2 + 1;

        while (leftChild < length) {
            // 取左右子节点较大一个
            if (leftChild + 1 < length && nums[leftChild + 1] > nums[leftChild]) {
                leftChild++;
            }

            // 当前根节点大于较大的子节点，则不用交换，当前大顶堆是正确的
            if (temp > nums[leftChild]) {
                break;
            }

            // 替换根节点
            nums[root] = nums[leftChild];
            root = leftChild;

            // 再拿原先的根节点和原先根节点的较大子节点的左子节点比较（下沉的过程）
            leftChild = 2 * root + 1;
        }
        // 现在我们的根节点成功找到了它的位置，给它赋值
        nums[root] = temp;
        System.out.printf("调整堆数据后：%s", Arrays.toString(nums));
        System.out.println();
    }
}
