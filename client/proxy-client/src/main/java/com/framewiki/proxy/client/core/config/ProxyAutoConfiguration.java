package com.framewiki.proxy.client.core.config;

import com.framewiki.proxy.client.core.WikiProxyClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.server.core.config
 * @ClassName: AgentAutoConfiguration
 * @Description: Socket 自动配置
 * @Author: frank tiger
 * @Date: 2025/1/3 16:10
 * @Version: 1.0
 */
@Lazy(false)
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ProxyConfig.class)
@AutoConfigureAfter({WebClientAutoConfiguration.class})
@ConditionalOnBean(ProxyMarkerConfiguration.Marker.class)
public class ProxyAutoConfiguration {

	/**
	 * Socket 配置
	 */
	private final ProxyConfig proxyConfig;

	/**
	 * 创建 bean
	 *
	 * @return 返回结果
	 */
	@Bean(initMethod = "start")
	@ConditionalOnMissingBean
	public WikiProxyClient nettySocketServer() {
		return new WikiProxyClient(proxyConfig);
	}
}
