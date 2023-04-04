package cn.zhanghui.demo.daily;


/**
 * @author ZhangHui
 * @version 1.0
 * @className BaseTest
 * @description 基于Main方法的简单测试
 * @date 2020/4/23
 */
public class BaseTest {

    private static int size = 0;

    public static void main(String[] args) {
        // class的唯一性测试
        //		Class clazz = Integer.class;
        //		Class clazz1 = Class.forName("java.lang.Integer");
        //
        //		System.out.println(clazz == clazz1); // true

        // map类key为空测试
        //		HashMap hashMap = new HashMap();
        //		Hashtable hashtable = new Hashtable<>();
        //		TreeMap treeMap = new TreeMap();
        //		ConcurrentHashMap concurrentHashMap  = new ConcurrentHashMap();
        //
        //		hashMap.put(null,null); // ok
        //		hashtable.put(null, null); // NullPointerException
        //		treeMap.put(null,null); // NullPointerException
        //		concurrentHashMap.put(null,null); // NullPointerException

        // HeapOverflow 测试
        //        URL url = null;
        //        List<ClassLoader> classLoaderList = new ArrayList<>();
        //        try {
        //            url = new URL("http://www.baidu.com");
        //        } catch (MalformedURLException e) {
        //            e.printStackTrace();
        //        }
        //        URL urls[] = {url};
        //        try {
        //            while(true){
        //                ClassLoader classloader = new URLClassLoader(urls);
        //                classLoaderList.add(classloader);
        //                classloader.loadClass("cn.zhanghui.demo.daily.base.BeansCopyTest");
        //            }
        //        } catch (ClassNotFoundException e) {
        //            e.printStackTrace();
        //        }

        //         常量池测试

//        		  Integer i1 = 128;
//        		  Integer i2 = 128;
//        		  System.out.println(i1 == i2); // false
        Integer i1 = 20;
        Integer i2 = 20;
        System.out.println(i1 == i2); // true

        Integer i3 = 0;
        Integer i4 = new Integer(20);
        Integer i5 = new Integer(20);
        Integer i6 = new Integer(0);
        System.out.println("i1 == i2：" + (i1 == i2)); // true
        System.out.println("i1 == i2 + i3：" + (i1 == i2 + i3)); // true
        System.out.println("i4 == i5：" + (i4 == i5));  // false
        System.out.println("i4 == i5 + i6：" + (i4 == i5 + i6)); // true 算术运算符 会拆箱进行运算 对于运算结果 在常量池里有的话 就直接返回
    }

}
