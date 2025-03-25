package com.framewiki.network.proxy.api;

import com.framewiki.network.proxy.model.HttpRoute;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.api
 * @ClassName: IHttpRouting
 * @Description: http 路由器
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public interface IHttpRouting {

	/**
	 * 获取有效路由
	 *
	 * @param host 主机
	 * @return 路由
	 */
	HttpRoute pickEffectiveRoute(String host);

}
