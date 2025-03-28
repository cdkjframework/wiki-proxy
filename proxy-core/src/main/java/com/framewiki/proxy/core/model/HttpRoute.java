package com.framewiki.proxy.core.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.model
 * @ClassName: HttpRoute
 * @Description: http路由表
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
@Getter
@Setter(AccessLevel.PRIVATE)
public class HttpRoute {

	/**
	 * 主路由，如果是多个则会去队列最后设置的那个
	 */
	private boolean master;
	/**
	 * 请求时的域名host
	 */
	private String host;
	/**
	 * 目标IP
	 */
	private String destIp;
	/**
	 * 目标端口
	 */
	private Integer destPort;

	/**
	 * 创建一个路由
	 *
	 * @param host     请求时的域名host
	 * @param destIp   目标IP
	 * @param destPort 目标端口
	 * @return Http路由
	 */
	public static HttpRoute of(String host, String destIp, Integer destPort) {
		return HttpRoute.of(false, host, destIp, destPort);
	}

	/**
	 * 创建一个路由
	 *
	 * @param master   是否主路由
	 * @param host     请求时的域名host
	 * @param destIp   目标IP
	 * @param destPort 目标端口
	 * @return Http路由
	 */
	public static HttpRoute of(boolean master, String host, String destIp, Integer destPort) {
		HttpRoute model = new HttpRoute();
		model.setMaster(master);
		model.setHost(host);
		model.setDestIp(destIp);
		model.setDestPort(destPort);
		return model;
	}

}
