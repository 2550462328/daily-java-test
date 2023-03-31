package cn.zhanghui.demo.daily.base.util;

import lombok.Data;

import java.io.*;

/**
 * @ClassName: SerializeTest.java
 * @Description: 查看java序列化码
 * @author: ZhangHui
 * @date: 2019年11月22日 上午9:15:46
 */
public class SerializeTest {
	
	public static void main(String[] args) {
		try {
			FileOutputStream fos = new FileOutputStream("C:\\Users\\Dell\\Desktop\\temp.out");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			SerializeUser user = new SerializeUser();
			oos.writeObject(user);
			oos.flush();
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

@Data
class SerializeUser implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String name = "zhanghui";
	private int password = 123456;
	
	private SerializeParent parent = new SerializeParent();
}

@Data
class SerializeParent implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String pversion;
}
