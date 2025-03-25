package com.framewiki.proxy.server.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.server.core.config
 * @ClassName: AgentConfig
 * @Description: 代理配置
 * @Author: frank tiger
 * @Date: 2025/1/3 16:11
 * @Version: 1.0
 */
@Data
@Component
@Configuration
@ConfigurationProperties(prefix = "spring.proxy.server")
public class ProxyConfig {

	/**
	 * 监听端口 默认为 8081
	 */
	private List<Integer> port = new ArrayList<>() {
		{
			add(8081);
		}
	};
	/**
	 * 目标端口  默认为 8081
	 */
	private List<Integer> destPort = new ArrayList<>() {
		{
			add(10010);
		}
	};

	/**
	 * 监听地址 默认为 127.0.0.1
	 */
	private List<String> ip = new ArrayList<>() {
		{
			add("127.0.0.1");
		}
	};

	/**
	 * 你的p12格式的证书路径
	 */
	private String sslKeyStorePath;

	/**
	 * 你的p12格式的证书密码
	 */
	private String sslKeyStorePassword;

	/**
	 * 证书类型
	 */
	private String sslKeyStoreType = "PKCS12";

	/**
	 * 协议 默认为 TLSv1.2 版本
	 */
	private String protocol = "TLSv1.2";

	/**
	 * 算法 默认为 sunx509
	 */
	private String algorithm = "sunx509";
	/**
	 * 交互密钥 AES
	 */
	private String aesKey = "8AUWlb+IWD+Fhbs0xnXCCg==";
	/**
	 * 交互签名key
	 */
	private String tokenKey = "tokenKey";
}
