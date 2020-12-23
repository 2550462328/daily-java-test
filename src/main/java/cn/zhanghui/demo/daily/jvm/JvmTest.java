package cn.zhanghui.demo.daily.jvm;

/**
 * 借助javaSDK自带的ManagementFactory可以查看jvm运行时的信息
 * @author ZhangHui
 * @date 2020/5/13
 * @return
 */
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.List;

public class JvmTest {
	public static void main(String[] args) {
		List<GarbageCollectorMXBean> l = ManagementFactory.getGarbageCollectorMXBeans();
		for(GarbageCollectorMXBean b : l) {
			System.out.println(b.getName());
		}

		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

		System.out.println(memoryMXBean.getHeapMemoryUsage().toString());

		System.out.println(memoryMXBean.getNonHeapMemoryUsage().toString());
	}
}
