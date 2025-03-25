package com.framewiki.proxy.client.core.side.client.handler;

import com.framewiki.proxy.client.core.side.client.adapter.ClientAdapter;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.client.core.side.client.handler
 * @ClassName: IClientHandler
 * @Description: 接收处理器
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public interface IClientHandler<R, W> {

	/**
	 * 执行方法
	 *
	 * @param model         接收的数据
	 * @param clientAdapter 客户端适配器
	 * @return boolean
	 * @throws Exception 抛出异常
	 */
	boolean proc(R model, ClientAdapter<? extends R, ? super W> clientAdapter) throws Exception;
}
