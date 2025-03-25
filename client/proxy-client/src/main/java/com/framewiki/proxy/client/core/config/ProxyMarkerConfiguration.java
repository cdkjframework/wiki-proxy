package com.framewiki.proxy.client.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.server.core.config
 * @ClassName: AgentMarkerConfiguration
 * @Description: java类作用描述
 * @Author: frank tiger
 * @Date: 2025/1/3 16:07
 * @Version: 1.0
 */
@Configuration(proxyBeanMethods = false)
public class ProxyMarkerConfiguration {

	/**
	 * 标记类
	 */
	@Bean
	public Marker socketMarker() {
		return new Marker();
	}

	/**
	 * 标记类
	 */
	public static class Marker {

	}
}
