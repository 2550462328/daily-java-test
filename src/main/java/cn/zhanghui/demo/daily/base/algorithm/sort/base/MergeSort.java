package cn.zhanghui.demo.daily.base.algorithm.sort.base;

/**
 * Description:
 * 归并排序
 * a、将列表按照对等的方式进行拆分
 * 　　b、拆分小最小快的时候，在将最小块按照原来的拆分，进行合并
 * 　　c、合并的时候，通过左右两块的左边开始比较大小。小的数据放入新的块中
 * 　　d、说明：简单一点就是先对半拆成最小单位，然后将两半数据合并成一个有序的列表。
 *
 * @author createdBy huizhang43.
 * @date createdAt 2023/3/30 16:40
 **/
public class MergeSort extends AbstractBaseSort {

    public static void main(String[] args) {
        new MergeSort().baseSort(true);
    }

    @Override
    void sort(int[] source, boolean asc) {
        splitAndMerge(source, 0, source.length - 1);
    }

    private void splitAndMerge(int[] source, int start, int end) {

        if (end <= start) {
            return;
        }

        //1. 尝试拆分最小单元  为1个或2个元素
        splitAndMerge(source, start, (start + end) / 2);
        splitAndMerge(source, (start + end) / 2 + 1, end);

        int leftCursor = start;
        int rightCursor = (start + end) / 2 + 1;
        int[] result = new int[end - start + 1];
        int i = 0;

        //2. 一直比较两边的值 较小值放到result里面去
        while (leftCursor <= (start + end) / 2 && rightCursor <= end) {
            if (source[leftCursor] < source[rightCursor]) {
                result[i++] = source[leftCursor];
                leftCursor++;
            } else {
                result[i++] = source[rightCursor];
                rightCursor++;
            }
        }

        //3. 左边  1234  右边 5678 ，那1234放进去了 5678还没动呢  需要手动补进去
        while (leftCursor <= (start + end) / 2 || rightCursor <= end) {
            if (leftCursor <= (start + end) / 2) {
                result[i++] = source[leftCursor];
                leftCursor++;
            } else {
                result[i++] = source[rightCursor];
                rightCursor++;
            }
        }

        //4. 放到源数组中
        for (int j = start; j <= end; j++) {
            source[j] = result[j - start];
        }
    }
}
