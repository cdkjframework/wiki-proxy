package com.framewiki.proxy.server.core.side.server.listen.serversocket;

import java.net.ServerSocket;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.side.server.listen.serversocket
 * @ClassName: ICreateServerSocket
 * @Description: 创建服务端口接口
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public interface ICreateServerSocket {

	/**
	 * 创建监听服务
	 *
	 * @param listenPort 监听端口
	 * @return ServerSocket
	 * @throws Exception 创建失败
	 */
    ServerSocket createServerSocket(int listenPort) throws Exception;
}
