package cn.zhanghui.demo.daily.base.util;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: BeansCopyTest.java
 * @Description: 测试spring的BeansUtil的copyProperty功能
 * @author: ZhangHui
 * @date: 2019年10月28日 下午1:40:40
 */

public class BeansCopyTest {
	public static void main(String[] args){
		BeansOutClass1 oc1 = new BeansOutClass1();
		oc1.setName("outClass1");
		
		BeansOutClass1.BeansInnerClass ic1 = oc1.new BeansInnerClass();
		ic1.setDesc("this is innerClass");
		
		// 虽然bpList里面的类也是不同类，但是由于泛型在编译期间会擦拭，即BeanPojo变成了一个map，所有不存在类型上的差异，也就可以直接copy了
		List<BeanPojo> bpList = new ArrayList<>();
		bpList.add(new BeanPojo("b1"));
		bpList.add(new BeanPojo("b2"));
		bpList.add(new BeanPojo("b3"));
		oc1.setBpList(bpList);
		
		BeansOutClass2 oc2 = new BeansOutClass2();
		BeansOutClass2.BeansInnerClass ic2 = oc2.new BeansInnerClass();
		
		BeanUtils.copyProperties(oc1, oc2);
		// 对于内部类 在外部类进行copyProperties的时候不认为这两个内部类是一个属性，所以不会copy
		BeanUtils.copyProperties(ic1, ic2);
		
		System.out.println(oc2.getName());
		System.out.println(ic2.getDesc());
		System.out.println(oc2.getBpList().size());
		System.out.println(oc2.getBpList().get(0).getName());
	}
}

class BeansOutClass1 {
	private String name;
	
	private List<BeanPojo> bpList;
	
	// 注意 在需要copy的property必须设置get/set方法
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<BeanPojo> getBpList() {
		return bpList;
	}

	public void setBpList(List<BeanPojo> bpList) {
		this.bpList = bpList;
	}


	class BeansInnerClass {
		private String desc ;

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}
	}
}

class BeansOutClass2 {
	private String name;
	
	private List<BeanPojo> bpList;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public List<BeanPojo> getBpList() {
		return bpList;
	}

	public void setBpList(List<BeanPojo> bpList) {
		this.bpList = bpList;
	}
	
	class BeansInnerClass {
		private String desc;
		
		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}
	}
}

class BeanPojo {
	private String name;
	
	public BeanPojo(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}