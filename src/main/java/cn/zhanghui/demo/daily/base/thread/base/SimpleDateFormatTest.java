package cn.zhanghui.demo.daily.base.thread.base;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName: SimpleDateFormatTest.java
 * @Description: SimpleDateFormat在多线程下是线程不安全的
 * 解决办法一每个线程生成一个SimpleDateFormat
 * 解决办法二 每个线程绑定一个SimpleDateFormat到ThreadLocal
 * 解决办法三 放弃SimpleDateFormat 使用joda-time库下的
 * @author: ZhangHui
 * @date: 2019年5月24日 下午4:30:30
 */
public class SimpleDateFormatTest {
    public static void main(String[] args) throws Exception {
        FormatThread formatThread1 = new FormatThread("2018-09-09 11:11:11");
        FormatThread formatThread2 = new FormatThread("2018-10-09 11:11:11");
        FormatThread formatThread3 = new FormatThread("2018-11-09 11:11:11");
        FormatThread formatThread4 = new FormatThread("2018-12-09 11:11:11");

        formatThread1.start();
        formatThread2.start();
        formatThread3.start();
        formatThread4.start();
    }
}

class FormatThread extends Thread {
    private ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<SimpleDateFormat>();
    private String dateString;

    public FormatThread(String dateString) {
        this.dateString = dateString;
    }

    @Override
    public void run() {
        try {
            sdf.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            Date date = sdf.get().parse(dateString);
            String newString = sdf.get().format(date);
            if (!newString.equals(dateString)) {
                System.out.println("本应该是" + dateString + " ,现在是" + newString);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

}