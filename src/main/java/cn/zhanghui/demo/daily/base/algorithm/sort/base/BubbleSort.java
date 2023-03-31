package cn.zhanghui.demo.daily.base.algorithm.sort.base;

/**
 * Description:
 * 冒泡排序
 * 　　a、冒泡排序，是通过每一次遍历获取最大/最小值
 * 　　b、将最大值/最小值放在尾部/头部
 * 　　c、然后除开最大值/最小值，剩下的数据在进行遍历获取最大/最小值
 *
 * @author createdBy huizhang43.
 * @date createdAt 2023/3/28 9:36
 **/
public class BubbleSort extends AbstractBaseSort {

    public static void main(String[] args) {
        new BubbleSort().baseSort(true);
    }

    @Override
    public void sort(int[] source, boolean asc) {
        for (int i = 0; i < source.length - 1; i++) { // 轮次
            for (int j = 0; j < source.length - i - 1; j++) { // 待比较值
                boolean condition = asc ^ source[j + 1] > source[j];
                if (condition) {
                    int temp = source[j];
                    source[j] = source[j + 1];
                    source[j + 1] = temp;
                }
            }
        }
    }

    /**
     * 减少外循环次数
     *
     * @param source
     * @param asc
     */
    private void sort1(int[] source, boolean asc) {
        boolean isSorted;
        for (int i = 0; i < source.length - 1; i++) { // 轮次
            isSorted = true;
            for (int j = 0; j < source.length - i - 1; j++) { // 待比较值
                boolean condition = asc ^ source[j + 1] > source[j];
                if (condition) {
                    int temp = source[j];
                    source[j] = source[j + 1];
                    source[j + 1] = temp;
                    isSorted = false;
                }
            }
            if (isSorted) {
                System.out.printf("已基本有序，经过轮次：%s", i);
                System.out.println();
                break;
            }
        }
    }

    /**
     * 减少内循环次数
     *
     * @param source
     * @param asc
     */
    private void sort2(int[] source, boolean asc) {
        boolean isSorted;
        // 默认内循环次数
        int lastExchangeIndex = source.length - 1;
        int sortBorder = 0;
        for (int i = 0; i < source.length - 1; i++) { // 轮次
            isSorted = true;
            for (int j = 0; j < lastExchangeIndex; j++) { // 待比较值
                boolean condition = asc ^ source[j + 1] > source[j];
                if (condition) {
                    int temp = source[j];
                    source[j] = source[j + 1];
                    source[j + 1] = temp;
                    isSorted = false;
                    sortBorder = j;
                    System.out.println("sortBorder变更为：" + sortBorder);
                }
            }
            if (isSorted) {
                System.out.printf("已基本有序，经过轮次：%s", i);
                System.out.println();
                break;
            }
            lastExchangeIndex = sortBorder;
        }
    }
}
