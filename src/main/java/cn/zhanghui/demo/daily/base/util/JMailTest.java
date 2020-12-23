package cn.zhanghui.demo.daily.base.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

/**
 * @ClassName: JMailTest.java
 * @Description: 发送带附件的邮件示例
 * @author: ZhangHui
 * @date: 2019年10月14日 上午10:56:02
 */
public class JMailTest {
    public static void main(String[] args) {
		Properties props = new Properties();
		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.transport.protocol", "smtp");
		
		Session session = Session.getInstance(props);
		session.setDebug(true);
		
		Message msg = new MimeMessage(session);
		try {
			/*邮件消息头设置*/
			msg.setFrom(new InternetAddress("2550462328@qq.com"));
			msg.setRecipients(RecipientType.TO,
					InternetAddress.parse("1730676894@qq.com"));
			msg.setSubject("From sohu 这是一封复杂邮件");
			/*邮件消息内容设置，包括两个附件和一段正文*/
			Multipart msgPart = new MimeMultipart("mixed");
			msg.setContent(msgPart);
			MimeBodyPart body = new MimeBodyPart(); //表示正文
			MimeBodyPart attach1 = new MimeBodyPart(); //表示附件1
			MimeBodyPart attach2 = new MimeBodyPart(); //表示附件2
			msgPart.addBodyPart(body);
			msgPart.addBodyPart(attach1);
			msgPart.addBodyPart(attach2);
			/*以下为设置正文*/
			/*正文是文字和图片混合的*/
			Multipart contentPart = new MimeMultipart("related");
			body.setContent(contentPart);
			MimeBodyPart content = new MimeBodyPart(); //文字
			MimeBodyPart img = new MimeBodyPart(); //图片
			contentPart.addBodyPart(content);
			contentPart.addBodyPart(img);
			ByteArrayDataSource fileds = new ByteArrayDataSource(new FileInputStream("C:\\Users\\Administrator\\Desktop\\img\\QQ图片20180704124218.jpg"),
					"application/octet-stream");
			DataHandler imgDataHandler = new DataHandler(fileds);
			//		DataSource imgds = new FileDataSource("D:\\picture\\jpg\\touxiang.jpg");
			//		DataHandler imgDataHandler = new DataHandler(imgds);
			img.setDataHandler(imgDataHandler);
			//注意：Content-ID的属性值一定要加上<>，不能是touxiang.jpg
			img.setHeader("Content-ID", "<touxiang.jpg>");
			//为图片设置文件名，有的邮箱会把html内嵌的图片也当成附件
			img.setFileName("touxianga.jpg");
			//设置文字内容
			/**
			 * 注意：在html代码中要想显示刚才的touxiang.jpg
			 * src里不能直接写Content-ID的值，要用cid:这种方式
			 */
			content.setContent(
					"<div style='color:red;font-size:18px;'>从sohu发来的邮件</div>：我这里有一张图片<img src='cid:touxiang.jpg' alt='touxiang' width=\"100px\" height='100px' />,好看吗？",
					"text/html;charset=utf-8");
			/*正文内容设置结束*/
			/*下面为设置附件*/
			attach1.setDataHandler(new DataHandler(new FileDataSource("C:\\Users\\Administrator\\Desktop\\zhanghui.txt")));
			attach1.setFileName("file1.txt");
			attach2.setDataHandler(new DataHandler(new FileDataSource("C:\\Users\\Administrator\\Desktop\\pcc.txt")));
			attach2.setFileName("file2.txt");
			msg.saveChanges();
			//把邮件以文件的形式写入到磁盘
			OutputStream os = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\demo.eml");
			msg.writeTo(os);
			os.close();
			Transport trans = session.getTransport();
			trans.connect("smtp.qq.com", 25, "2550462328", "blrdfungdsigecdf");
			trans.sendMessage(msg, msg.getAllRecipients());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
