package com.framewiki.proxy.server.core.side.server.client.adapter;

import java.net.Socket;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.side.server.client.adapter
 * @ClassName: ClientServiceAdapter
 * @Description: Client service adapter
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public interface ClientServiceAdapter {

	/**
	 * 处理方法
	 *
	 * @param listenSocket 监听socket
	 * @throws Exception 异常
	 */
	void procMethod(Socket listenSocket) throws Exception;

}
