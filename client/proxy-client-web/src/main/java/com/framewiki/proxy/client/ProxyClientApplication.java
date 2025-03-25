package com.framewiki.proxy.client;

import com.cdkjframework.core.spring.CdkjApplication;
import com.framewiki.proxy.client.core.annotation.EnableAutoProxyClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.client
 * @ClassName: ClientAppliaction
 * @Description: ClientApplication
 * @Author: xiaLin
 * @Date: 2025/1/3 17:04
 * @Version: 1.0
 */
@EnableAutoProxyClient
@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class
})
public class ProxyClientApplication {

	/**
	 * 程序入口
	 *
	 * @param args 启动参数
	 */
	public static void main(String[] args) {
		CdkjApplication.run(ProxyClientApplication.class, args);
	}
}
