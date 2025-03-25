package com.framewiki.proxy.server;

import com.cdkjframework.core.spring.CdkjApplication;
import com.framewiki.proxy.server.core.annotation.EnableAutoProxyServer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.server
 * @ClassName: ServerApplication
 * @Description: 服务启动类
 * @Author: frank tiger
 * @Date: 2025/1/2 13:59
 * @Version: 1.0
 */

@EnableAutoProxyServer
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class
})
public class ProxyServerApplication {

  /**
   * 程序入口
   *
   * @param args 启动参数
   */
  public static void main(String[] args) {
    CdkjApplication.run(ProxyServerApplication.class, args);
  }
}
