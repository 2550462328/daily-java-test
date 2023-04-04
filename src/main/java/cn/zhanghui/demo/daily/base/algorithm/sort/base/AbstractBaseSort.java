package cn.zhanghui.demo.daily.base.algorithm.sort.base;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Description:
 *
 * @author createdBy huizhang43.
 * @date createdAt 2023/3/30 15:14
 **/
public abstract class AbstractBaseSort {

    public void baseSort(boolean asc) {
        Scanner scanner = new Scanner(System.in);
        int length = scanner.nextInt();
        int[] source = new int[length];
        for (int i = 0; i < length; i++) {
            source[i] = scanner.nextInt();
        }
        System.out.println("排序前:" + Arrays.toString(source));
        sort(source, asc);
        System.out.println("排序后:" + Arrays.toString(source));
    }

    public abstract void sort(int[] source, boolean asc);
}
