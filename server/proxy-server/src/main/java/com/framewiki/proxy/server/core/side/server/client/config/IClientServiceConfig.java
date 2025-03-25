package com.framewiki.proxy.server.core.side.server.client.config;

import com.framewiki.network.proxy.channel.impl.BaseSocketChannel;
import com.framewiki.proxy.server.core.side.server.client.adapter.ClientServiceAdapter;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.side.server.client.config
 * @ClassName: IClientServiceConfig
 * @Description: 客户端服务配置
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public interface IClientServiceConfig<R, W> {

	/**
	 * 监听端口
	 *
	 * @return
	 */
	Integer getListenPort();

	/**
	 * 创建监听端口
	 *
	 * @return
	 * @throws Exception
	 */
	ServerSocket createServerSocket() throws Exception;

	/**
	 * 客户端适配器
	 *
	 * @return
	 */
	ClientServiceAdapter getClientServiceAdapter();

	/**
	 * 交互通道
	 *
	 * @param listenSocket
	 * @return
	 * @throws Exception
	 */
	BaseSocketChannel<? extends R, ? super W> newSocketChannel(Socket listenSocket) throws Exception;

	/**
	 * 字符集
	 *
	 * @return
	 */
	Charset getCharset();

}
