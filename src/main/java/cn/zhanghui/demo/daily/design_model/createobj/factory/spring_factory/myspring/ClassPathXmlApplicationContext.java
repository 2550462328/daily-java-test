package cn.zhanghui.demo.daily.design_model.createobj.factory.spring_factory.myspring;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @ClassName: SpringFactory.java
 * @Description: spring工厂类，用于获取类
 * @author: ZhangHui
 * @date: 2019年10月14日 上午9:52:35
 */
public class ClassPathXmlApplicationContext implements BeanFactory {

	private Map<String, Object> container = new HashMap<>();

	public ClassPathXmlApplicationContext(String xmlPath) throws Exception {
		SAXReader reader = new SAXReader();
		File file = new File(Thread.currentThread().getContextClassLoader().getResource(xmlPath).getPath());
		Document document = reader.read(file);
		Element root = document.getRootElement();
		List<Element> list = root.elements();
		for (Element ele : list) {
			Object bean = Class.forName(ele.attributeValue("class")).newInstance();
			container.put(ele.attributeValue("id"), bean);
		}
		for(Entry<String, Object> entry : container.entrySet()) {
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
		}
	}

	@Override
	public Object getBean(String name) {
		return container.containsKey(name) ? container.get(name) : null;
	}

}
