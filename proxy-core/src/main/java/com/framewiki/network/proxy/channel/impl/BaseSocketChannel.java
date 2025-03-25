package com.framewiki.network.proxy.channel.impl;

import com.framewiki.network.proxy.channel.Channel;

import java.io.IOException;
import java.net.Socket;

/**
 * @param <R> 通道读取的类型
 * @param <W> 通道写入的类型
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.channel
 * @ClassName: BaseSocketChannel
 * @Description: socket通道
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public abstract class BaseSocketChannel<R, W> implements Channel<R, W> {

	/**
	 * 获取socket
	 *
	 * @return socket
	 */
	public abstract Socket getSocket();

	/**
	 * 设置socket
	 *
	 * @param socket socket
	 * @throws IOException io异常
	 */
	public abstract void setSocket(Socket socket) throws IOException;

	/**
	 * 关闭socket
	 *
	 * @throws IOException io异常
	 */
	public abstract void closeSocket() throws IOException;

	/**
	 * 关闭通道
	 *
	 * @throws IOException io异常
	 */
	@Override
	public void close() throws IOException {
		this.closeSocket();
	}

}
