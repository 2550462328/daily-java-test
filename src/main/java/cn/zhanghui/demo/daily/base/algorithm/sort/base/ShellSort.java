package cn.zhanghui.demo.daily.base.algorithm.sort.base;

/**
 * Description:
 * 希尔排序
 * 　  a、基本上和插入排序一样的道理
 * 　　b、不一样的地方在于，每次循环的步长，通过减半的方式来实现
 * 　　c、说明：基本原理和插入排序类似，不一样的地方在于。通过间隔多个数据来进行插入排序。
 *
 * @author createdBy huizhang43.
 * @date createdAt 2023/3/31 10:26
 **/
public class ShellSort extends AbstractBaseSort {

    public static void main(String[] args) {
        new ShellSort().baseSort(true);
    }

    @Override
    void sort(int[] source, boolean asc) {

        int length = source.length;
        //1. 确定步长 这里采用折叠的步长计算
        //step = 1 就是插入排序
        for (int step = length / 2; step > 0; step /= 2) {
            //2. i在前面跑
            for (int i = step; i < length; i++) {
                // 3. j在后面比较 比较的跨度是 step 即  0 ... step ... step * 2 ...step * 3
                for (int j = i; j >= step; j -= step) {
                    boolean changeValue = asc ^ source[j] > source[j - step];
                    if (changeValue) {
                        int temp = source[j];
                        source[j] = source[j - step];
                        source[j - step] = temp;
                    } else {
                        // 4. 因为排序后基于跨度的排列是有序的  不需要替换的时候 就不需要继续向下比较了
                        break;
                    }
                }
            }
        }
    }
}
