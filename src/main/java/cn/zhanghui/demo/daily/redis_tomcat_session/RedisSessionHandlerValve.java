package cn.zhanghui.demo.daily.redis_tomcat_session;


import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

import lombok.extern.log4j.Log4j;

@Log4j
public class RedisSessionHandlerValve extends ValveBase {
  private RedisSessionManager manager;

  public void setRedisSessionManager(RedisSessionManager manager) {
    this.manager = manager;
  }

  @Override
  public void invoke(Request request, Response response) throws IOException, ServletException {
    try {
      getNext().invoke(request, response);
    } finally {
      manager.afterRequest();
    }
  }
}
