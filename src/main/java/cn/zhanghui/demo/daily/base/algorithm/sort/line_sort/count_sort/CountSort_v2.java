package cn.zhanghui.demo.daily.base.algorithm.sort.line_sort.count_sort;

import java.util.Arrays;

/**
 * @ClassName: CountSort1.java
 * @Description: 这是一个计数排序的稳定排序算法，记住放入的顺序，在数值一样时根据放入顺序排序
 *               计数排序的算法复杂度是o(n+m)  n是排序数组长度 m是统计数组长度 空间复杂度是o(m)
 * @author: ZhangHui
 * @date: 2019年11月5日 上午8:53:44
 */
public class CountSort_v2 {
	
	public static int[] countSort(int[] arrayToSort) {
		int max = arrayToSort[0], min = arrayToSort[0]; 
		for(int i = 1; i < arrayToSort.length; i++) { 
			if(arrayToSort[i] > max) {
				max = arrayToSort[i];
			}else if(arrayToSort[i] < min){
				min = arrayToSort[i];
			}
		}
		
		int[] container = new int[(max - min) + 1]; 
		for(int i = 0; i < arrayToSort.length; i++) { 
			container[arrayToSort[i] - min]++;
		}
		
		// 关键一：将后面的元素 = 自身值 + 前面的元素之和
		// 这样的目的是为了 让统计数组存储的元素值，等于相应整数的最终排序位置
		// 可以理解成当有重复数据的时候，先放入的放在下面，遍历的时候，放在下面的就排位靠前
		int sum = 0;
		for(int i = 0; i < container.length; i++) {
			sum += container[i];
			container[i] = sum;
		}
		
		// 关键二：倒序遍历原始数列，从统计数组找到正确位置，输出到结果数组
		int[] arraySorted = new int[arrayToSort.length]; 
		for(int i = arrayToSort.length - 1; i >= 0; i--) {
			arraySorted[container[arrayToSort[i] - min] - 1] = arrayToSort[i];
			container[arrayToSort[i]-min]--;
		}
		
		return arraySorted;
	}
	
	public static void main(String[] args) {
//		int[] arrayToSort = new int[] {2,4,3,1,6,7,4,9,10,2,1,0,1,2,3};
		int[] arrayToSort = new int[] {98,93,92,95,102};
		int[] arraySorted = countSort(arrayToSort);
		System.out.println(Arrays.toString(arraySorted));
	}
}
