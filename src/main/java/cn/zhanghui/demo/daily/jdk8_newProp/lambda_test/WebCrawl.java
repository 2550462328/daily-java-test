package cn.zhanghui.demo.daily.jdk8_newProp.lambda_test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author: ZhangHui
 * @description: 简单的网络爬虫，使用lambda表达式进行操作
 * 这里我们要简洁化lambda表达式中的checkException
 * @date: 2019/6/27
 */
public class WebCrawl {
    public static void main(String[] args) {
        List<String> urlList = Arrays.asList("http://www.baidu.com", "http://www.toutiao.com");
        WebCrawl webCrawl = new WebCrawl();
        webCrawl.crawl(urlList);
    }
    //  开始执行爬虫
    public void crawl(List<String> urlList){
        urlList.stream()
                .map(FunctionCheck.unchecked(urlStr -> new URL(urlStr)))
                .forEach(ConsumerCheck.uncheck(url -> save(url)));
    }

    public void save(URL url) throws IOException{
        String uuid = UUID.randomUUID().toString();
        InputStream is = url.openConnection().getInputStream();
        Files.copy(is, Paths.get(uuid + ".txt"), StandardCopyOption.REPLACE_EXISTING);
    }
}
