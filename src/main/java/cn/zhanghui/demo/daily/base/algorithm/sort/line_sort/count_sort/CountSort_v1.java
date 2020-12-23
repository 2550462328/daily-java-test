package cn.zhanghui.demo.daily.base.algorithm.sort.line_sort.count_sort;

import java.util.Arrays;

/**
 * @ClassName: CountSort1.java
 * @Description: 这是一个简单的计数排序，实现0-20之间数字的排序
 *               计数排序的算法复杂度是o(n+m)  n是排序数组长度 m是统计数组长度 空间复杂度是o(m)
 *               计数排序不适用的场景
 *                   1、当数列最大最小值差距过大时
 *                   2、当数列元素不是整数 
 * @author: ZhangHui
 * @date: 2019年11月5日 上午8:53:44
 */
public class CountSort_v1 {
	
	public static int[] countSort(int[] arrayToSort) {
		int max = arrayToSort[0], min = arrayToSort[0]; 
		for(int i = 1; i < arrayToSort.length; i++) { // 使用max - min + 1的值作为container的长度
			if(arrayToSort[i] > max) {
				max = arrayToSort[i];
			}else if(arrayToSort[i] < min){
				min = arrayToSort[i];
			}
		}
		
		int[] container = new int[(max - min) + 1]; // 将待排序数组的值依次对应container的下标
//		for(int i = 0; i < container.length; i++) {
//			for(int j = 0; j < arrayToSort.length; j++) {
//				if((i + min) == arrayToSort[j]) {
//					container[i]++;
//				}
//			}
//		}
		for(int i = 0; i < arrayToSort.length; i++) { // 上面的步骤可以简写成这个
			container[arrayToSort[i] - min]++;
		}
		
		System.out.println("container.length = " + container.length);
		
		int[] arraySorted = new int[arrayToSort.length]; // 从头遍历container，输出到arraySorted中即是排序
		int k = 0;
		
		for(int i = 0; i < container.length; i++) {
			for(int j = 0; j < container[i]; j++) {
				arraySorted[k++] = i + min;
			}
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
