package com.framewiki.proxy.server.core.side.server.client.config;

import com.framewiki.proxy.server.core.side.server.client.adapter.impl.DefaultReadAheadPassValueAdapter;
import com.framewiki.network.proxy.channel.impl.InteractiveChannelBase;
import com.framewiki.network.proxy.channel.impl.BaseSocketChannel;
import com.framewiki.network.proxy.model.InteractiveModel;
import com.framewiki.proxy.server.core.side.server.client.adapter.ClientServiceAdapter;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.side.server.client.config
 * @ClassName: SimpleClientServiceConfig
 * @Description: 简单交互的客户端服务配置
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
@NoArgsConstructor
public class SimpleClientServiceConfig implements IClientServiceConfig<InteractiveModel, InteractiveModel> {

	/**
	 * 监听端口
	 */
	private Integer listenPort;

	/**
	 * 适配器
	 */
	private ClientServiceAdapter clientServiceAdapter = new DefaultReadAheadPassValueAdapter(this);

	/**
	 * 字符编码
	 */
	private Charset charset = StandardCharsets.UTF_8;

	/**
	 * 构造方法
	 */
	public SimpleClientServiceConfig(Integer listenPort) {
		this.listenPort = listenPort;
	}

	/**
	 * 获取监听端口
	 *
	 * @return 监听端口
	 */
	@Override
	public Integer getListenPort() {
		return this.listenPort;
	}

	/**
	 * 设置监听端口
	 *
	 * @param listenPort 监听端口
	 */
	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}

	/**
	 * 创建服务端监听socket
	 *
	 * @return ServerSocket
	 * @throws Exception 创建失败
	 */
	@Override
	public ServerSocket createServerSocket() throws Exception {
		ServerSocketChannel openServerSocketChannel = SelectorProvider.provider().openServerSocketChannel();
		openServerSocketChannel.bind(new InetSocketAddress(this.getListenPort()));
		return openServerSocketChannel.socket();
	}

	/**
	 * 获取适配器
	 *
	 * @return 适配器
	 */
	@Override
	public ClientServiceAdapter getClientServiceAdapter() {
		return this.clientServiceAdapter;
	}

	/**
	 * 设置适配器
	 *
	 * @param clientServiceAdapter 适配器
	 */
	public void setClientServiceAdapter(ClientServiceAdapter clientServiceAdapter) {
		this.clientServiceAdapter = clientServiceAdapter;
	}

	/**
	 * 创建socket通道
	 *
	 * @param listenSocket 监听socket
	 * @return socket通道
	 * @throws Exception 创建失败
	 */
	@Override
	public BaseSocketChannel<? extends InteractiveModel, ? super InteractiveModel> newSocketChannel(Socket listenSocket)
			throws Exception {
		InteractiveChannelBase channel = new InteractiveChannelBase();
		channel.setSocket(listenSocket);
		channel.setCharset(this.charset);
		return channel;
	}

	/**
	 * 获取字符编码
	 *
	 * @return 字符编码
	 */
	@Override
	public Charset getCharset() {
		return this.charset;
	}

	/**
	 * 设置字符编码
	 *
	 * @param charset 字符编码
	 */
	public void setCharset(Charset charset) {
		this.charset = charset;
	}

}
