package com.framewiki.proxy.core.api;

import com.framewiki.proxy.core.model.HttpRoute;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.api
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
