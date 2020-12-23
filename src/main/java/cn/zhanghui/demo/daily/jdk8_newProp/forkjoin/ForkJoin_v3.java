package cn.zhanghui.demo.daily.jdk8_newProp.forkjoin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @ClassName: ForkJoin_v2.java
 * @Description: 这是jdk1.8 使用Stream的并行流的排序方式（实质也是fork/join）
 * @author: ZhangHui
 * @date: 2019年11月6日 下午5:16:17
 */
public class ForkJoin_v3 {
	public static Integer[] parallelSort(Integer[] array) {
		 List<Integer> tmp = new ArrayList<Integer>();
		 Collections.addAll(tmp, array);
	     List<Integer> tmp1 = tmp.stream().parallel().mapToInt(Integer::intValue).sorted().boxed().collect(Collectors.toList());
	     return tmp1.toArray(new Integer[0]);
	}
}
