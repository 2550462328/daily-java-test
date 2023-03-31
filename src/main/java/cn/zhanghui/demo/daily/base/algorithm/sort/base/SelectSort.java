package cn.zhanghui.demo.daily.base.algorithm.sort.base;

/**
 * Description:
 * 选择排序
 * 　　a、将第一个值看成最小值
 * 　　b、然后和后续的比较找出最小值和下标
 * 　　c、交换本次遍历的起始值和最小值
 * 　　d、说明：每次遍历的时候，将前面找出的最小值，看成一个有序的列表，后面的看成无序的列表，然后每次遍历无序列表找出最小值
 *
 * @author createdBy huizhang43.
 * @date createdAt 2023/3/31 10:02
 **/
public class SelectSort extends AbstractBaseSort {

    public static void main(String[] args) {
        new SelectSort().baseSort(false);
    }

    @Override
    void sort(int[] source, boolean asc) {
        for (int i = 0; i < source.length; i++) {
            // 1. 选择第一个值做最小/大值
            int limitValue = source[i];
            int limitIndex = i;
            for (int j = i + 1; j < source.length; j++) {
                boolean changeValue = asc ^ source[j] > limitValue;
                // 2. 寻找比它大/小的值替代它
                if (changeValue) {
                    limitValue = source[j];
                    limitIndex = j;
                }
            }
            // 3. 把最大/小值放在第一位 实现顺序排列
            if (limitIndex != i) {
                source[limitIndex] = source[i];
                source[i] = limitValue;
            }
        }
    }
}
