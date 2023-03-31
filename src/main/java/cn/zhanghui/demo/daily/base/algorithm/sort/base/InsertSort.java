package cn.zhanghui.demo.daily.base.algorithm.sort.base;

/**
 * Description:
 * 插入排序
 * 　　a、默认从第二个数据开始比较。
 * 　　 b、如果第二个数据比第一个小，则交换。然后在用第三个数据比较，如果比前面小，则插入（狡猾）。否则，退出循环
 * 　　 c、说明：默认将第一数据看成有序列表，后面无序的列表循环每一个数据，如果比前面的数据小则插入（交换）。否则退出。
 *
 * @author createdBy huizhang43.
 * @date createdAt 2023/3/30 15:09
 **/
public class InsertSort extends AbstractBaseSort {

    public static void main(String[] args) {
        new InsertSort().baseSort(true);
    }

    @Override
    public void sort(int[] source, boolean asc) {
        // 基于一个有序数组 中放入一个元素
        for (int i = 1; i < source.length; i++) {
            // 确定放入元素的位置
            for (int j = i; j > 0; j--) {
                boolean needChange = asc ^ source[j - 1] < source[j];
                if (needChange) {
                    int temp = source[j - 1];
                    source[j - 1] = source[j];
                    source[j] = temp;
                } else {
                    break;
                }
            }
        }
    }
}
