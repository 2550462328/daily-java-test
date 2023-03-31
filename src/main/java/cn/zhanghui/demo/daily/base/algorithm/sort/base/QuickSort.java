package cn.zhanghui.demo.daily.base.algorithm.sort.base;

/**
 * Description:
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
 * @author createdBy huizhang43.
 * @date createdAt 2023/3/30 19:34
 **/
public class QuickSort extends AbstractBaseSort {

    public static void main(String[] args) {
        new QuickSort().baseSort(true);
    }

    @Override
    void sort(int[] source, boolean asc) {
        binarySort(source, 0, source.length - 1);
    }

    private void binarySort(int[] source, int start, int end) {
        if (end <= start) {
            return;
        }
        //1. 随机挑选一个幸运值
        int middleValue = source[start];
        boolean isPositive = false;
        int leftCursor = start, rightCursor = end;

        // 2. 比幸运值小的 放左边；比幸运值大的 放右边
        // 借助幸运值的下标为跳板 存放需要调换的值，那么原本被调换的值就空了，作为下一个跳板
        // 比如 幸运值下标a   现在下标b 比幸运值小 把b的值 给a ,那么现在b的值其实已经被拿走了，那么下一次逆向遍历如果找到比a的值大的c，就可以拿到b的位置
        // 最后 总会有一个地方是空的  诶 刚巧我们一开始选取了一个幸运值（中间值），没错把它放进去就可以了，这样它左边的值比它小，右边的值比它大
        while (leftCursor < rightCursor) {
            if (isPositive) {
                if (source[leftCursor] > middleValue) {
                    source[rightCursor] = source[leftCursor];
                    rightCursor--;
                    isPositive = false;
                } else {
                    leftCursor++;
                }
            } else {
                if (source[rightCursor] < middleValue) {
                    source[leftCursor] = source[rightCursor];
                    leftCursor++;
                    isPositive = true;
                } else {
                    rightCursor--;
                }
            }
        }

        //3. 把幸运值放到 空的地方(最近一次发生替换的地方)
        source[leftCursor] = middleValue;

        //4. 分别对幸运值的左边 和 右边进行二分排序
        binarySort(source, start, leftCursor);
        binarySort(source, leftCursor + 1, end);
    }
}
