package cn.zhanghui.demo.daily.base.singleton;

import java.io.Serializable;

/**
 * 
 * @ClassName: SingletonTest3.java
 * @Description: 解决在序列化和反序列化返回的不是一个对象
 * @author: ZhangHui
 * @date: 2019年5月24日 上午11:33:58
 */
public class SingletonTest3 {
	public static void main(String[] args) {
		try {
			UniqueObject3 object = UniqueObject3.getInstance();
			System.out.println(object.hashCode());
			UniqueObject3 objectClone = (UniqueObject3)object.clone();
			System.out.println(objectClone.hashCode());
//			File file = new File("C:\\Users\\Dell\\Desktop\\test.txt");
//			FileOutputStream fos = new FileOutputStream(file);
//			ObjectOutputStream oos = new ObjectOutputStream(fos);
//			oos.writeObject(object);
//			oos.close();
//			fos.close();
//			System.out.println(object.hashCode());
//			
//			FileInputStream fis = new FileInputStream(file);
//			ObjectInputStream  ois = new ObjectInputStream(fis);
//			UniqueObject3 object1 = (UniqueObject3)ois.readObject();
//			ois.close();
//			fis.close();
//			System.out.println(object1.hashCode());
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
}
class UniqueObject3 implements Serializable, Cloneable {
	private static final long serialVersionUID = 6418567389685195295L;
	
	private static class staticObject{
		 private static UniqueObject3 object = new UniqueObject3();
	}
	public static UniqueObject3 getInstance() {
		return staticObject.object;
	}
	protected Object readResolve() {
		System.out.println("调用了readResolve方法！");
		return staticObject.object;
	}
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}

 class ThreadC implements Runnable {
	@Override
	public void run() {
		UniqueObject3 object = UniqueObject3.getInstance();
		System.out.println(object.hashCode());
	}
}