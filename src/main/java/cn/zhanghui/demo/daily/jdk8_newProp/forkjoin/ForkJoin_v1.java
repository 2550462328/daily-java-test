package cn.zhanghui.demo.daily.jdk8_newProp.forkjoin;

/**
 * @ClassName: ForkJoin_v1.java
 * @Description: 快速排序对数组进行排序
 * @author: ZhangHui
 * @date: 2019年11月6日 下午5:15:25
 */
public class ForkJoin_v1 {
//	public static int[] quickSort(int arr[],int start,int end) {
//			int pivot = arr[start];        
//		    int i = start;        
//		    int j = end;        
//		    while (i<j) {            
//		        while ((i<j)&&(arr[j]>pivot)) {                
//		            j--;            
//		        }            
//		        while ((i<j)&&(arr[i]<pivot)) {                
//		            i++;            
//		        }            
//		        if ((arr[i]==arr[j])&&(i<j)) {                
//		            i++;            
//		        } else {                
//		            int temp = arr[i];                
//		            arr[i] = arr[j];                
//		            arr[j] = temp;            
//		        }        
//		    }        
//		    if (i-1>start) arr=quickSort(arr,start,i-1);        
//		    if (j+1<end) arr=quickSort(arr,j+1,end);        
//		    return arr;    
//	}
    /** The method for sorting the numbers */
    public static void mergeSort(int[] list) {
      if (list.length > 1) {
        // Merge sort the first half
        int[] firstHalf = new int[list.length / 2];
        System.arraycopy(list, 0, firstHalf, 0, list.length / 2);
        mergeSort(firstHalf);

        // Merge sort the second half
        int secondHalfLength = list.length / 2;
        int[] secondHalf = new int[secondHalfLength];
        System.arraycopy(list, list.length / 2,
          secondHalf, 0, secondHalfLength);
        mergeSort(secondHalf);

        // Merge firstHalf with secondHalf into list
        merge(firstHalf, secondHalf, list);
      }
    }

    /** Merge two sorted lists */
    public static void merge(int[] list1, int[] list2, int[] temp) {
      int current1 = 0; // Current index in list1
      int current2 = 0; // Current index in list2
      int current3 = 0; // Current index in temp

      while (current1 < list1.length && current2 < list2.length) {
        if (list1[current1] < list2[current2])
          temp[current3++] = list1[current1++];
        else
          temp[current3++] = list2[current2++];
      }

      while (current1 < list1.length)
        temp[current3++] = list1[current1++];

      while (current2 < list2.length)
        temp[current3++] = list2[current2++];
    }
}
