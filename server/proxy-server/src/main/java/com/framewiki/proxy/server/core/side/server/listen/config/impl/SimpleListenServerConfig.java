package com.framewiki.proxy.server.core.side.server.listen.config.impl;

import com.alibaba.fastjson.JSONObject;
import com.framewiki.network.proxy.api.socket.part.BaseSocketPart;
import com.framewiki.network.proxy.api.socket.part.SimpleSocketPart;
import com.framewiki.network.proxy.channel.impl.InteractiveChannelBase;
import com.framewiki.network.proxy.channel.impl.BaseSocketChannel;
import com.framewiki.network.proxy.model.InteractiveModel;
import com.framewiki.proxy.server.core.side.server.listen.ServerListenThread;
import com.framewiki.proxy.server.core.side.server.listen.clear.IClearInvalidSocketPartThread;
import com.framewiki.proxy.server.core.side.server.listen.clear.impl.ClearInvalidSocketPartThread;
import com.framewiki.proxy.server.core.side.server.listen.config.ListenServerConfig;
import com.framewiki.proxy.server.core.side.server.listen.control.impl.ControlSocket;
import com.framewiki.proxy.server.core.side.server.listen.control.IControlSocket;
import com.framewiki.proxy.server.core.side.server.listen.recv.impl.ClientHeartHandler;
import com.framewiki.proxy.server.core.side.server.listen.recv.impl.CommonReplyHandler;
import com.framewiki.proxy.server.core.side.server.listen.serversocket.ICreateServerSocket;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.side.server.listen.config
 * @ClassName: SimpleListenServerConfig
 * @Description: 简单的交互、隧道；监听服务配置
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
@Slf4j
@Data
@NoArgsConstructor
public class SimpleListenServerConfig implements ListenServerConfig {

	/**
	 * 监听端口
	 */
	private Integer listenPort;

	/**
	 * 缓存无效时间
	 */
	private Long invaildMillis = 60000L;

	/**
	 * 清理无效时间间隔
	 */
	private Long clearInterval = 10L;

	/**
	 * 编码格式
	 */
	private Charset charset = StandardCharsets.UTF_8;

	/**
	 * 创建ServerSocket
	 */
	private ICreateServerSocket createServerSocket;

	/**
	 * 缓存流大小
	 */
	private int streamCacheSize = 8196;

	/**
	 * 构造方法
	 *
	 * @param listenPort 监听端口
	 */
	public SimpleListenServerConfig(Integer listenPort) {
		this.listenPort = listenPort;
	}

	/**
	 * 创建ServerSocket
	 *
	 * @return
	 * @throws Exception
	 */
	@Override
	public ServerSocket createServerSocket() throws Exception {
		if (this.createServerSocket == null) {
			ServerSocketChannel openServerSocketChannel = SelectorProvider.provider().openServerSocketChannel();
			openServerSocketChannel.bind(new InetSocketAddress(this.getListenPort()));
			return openServerSocketChannel.socket();
		} else {
			return this.createServerSocket.createServerSocket(this.getListenPort());
		}
	}

	/**
	 * 创建controlSocket使用channel
	 *
	 * @param socket socket
	 * @return
	 */
	protected BaseSocketChannel<? extends InteractiveModel, ? super InteractiveModel> newControlSocketChannel(
			Socket socket) {
		InteractiveChannelBase interactiveChannel;
		try {
			interactiveChannel = new InteractiveChannelBase(socket);
		} catch (IOException e) {
			log.error("newControlSocketChannel exception", e);
			return null;
		}
		return interactiveChannel;
	}

	@Override
	public IControlSocket newControlSocket(Socket socket, JSONObject config) {
		BaseSocketChannel<? extends InteractiveModel, ? super InteractiveModel> controlBaseSocketChannel = this
				.newControlSocketChannel(socket);
		ControlSocket controlSocket = new ControlSocket(controlBaseSocketChannel);
		controlSocket.addRecvHandler(CommonReplyHandler.INSTANCE);
		controlSocket.addRecvHandler(ClientHeartHandler.INSTANCE);
		return controlSocket;
	}

	@Override
	public IClearInvalidSocketPartThread newClearInvalidSocketPartThread(ServerListenThread serverListenThread) {
		ClearInvalidSocketPartThread clearInvalidSocketPartThread = new ClearInvalidSocketPartThread(
				serverListenThread);
		clearInvalidSocketPartThread.setClearIntervalSeconds(this.getClearInterval());
		return clearInvalidSocketPartThread;
	}

	@Override
	public BaseSocketPart newSocketPart(ServerListenThread serverListenThread) {
		SimpleSocketPart socketPart = new SimpleSocketPart(serverListenThread);
		socketPart.setInvalidMillis(this.getInvaildMillis());
		socketPart.setStreamCacheSize(this.getStreamCacheSize());
		return socketPart;
	}

}
