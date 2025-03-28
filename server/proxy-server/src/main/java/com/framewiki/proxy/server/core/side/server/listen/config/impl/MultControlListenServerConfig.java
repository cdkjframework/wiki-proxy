package com.framewiki.proxy.server.core.side.server.listen.config.impl;

import com.alibaba.fastjson.JSONObject;
import com.framewiki.proxy.server.core.side.server.listen.ServerListenThread;
import com.framewiki.proxy.server.core.side.server.listen.clear.IClearInvalidSocketPartThread;
import com.framewiki.proxy.server.core.side.server.listen.control.IControlSocket;
import com.framewiki.proxy.server.core.side.server.listen.control.impl.MultiControlSocket;
import com.framewiki.proxy.core.api.socket.part.BaseSocketPart;
import com.framewiki.proxy.server.core.side.server.listen.config.ListenServerConfig;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.side.server.listen.config
 * @ClassName: MultControlListenServerConfig
 * @Description: 多客户端；监听服务配置
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public class MultControlListenServerConfig implements ListenServerConfig {

	/**
	 * 监听配置
	 */
	protected final ListenServerConfig baseConfig;

	/**
	 * 多客户端控制
	 */
	protected final MultiControlSocket multControlSocket = new MultiControlSocket();

	/**
	 * 构建函数
	 */
	public MultControlListenServerConfig(ListenServerConfig baseConfig) {
		this.baseConfig = baseConfig;
	}

	/**
	 * 创建服务端监听
	 *
	 * @return ServerSocket
	 * @throws Exception 创建失败
	 */
	@Override
	public ServerSocket createServerSocket() throws Exception {
		return this.baseConfig.createServerSocket();
	}

	/**
	 * 创建控制连接
	 *
	 * @param socket TCP连接
	 * @param config 配置
	 * @return
	 */
	@Override
	public IControlSocket newControlSocket(Socket socket, JSONObject config) {
		IControlSocket controlSocket = this.baseConfig.newControlSocket(socket, config);
		multControlSocket.addControlSocket(controlSocket);
		return multControlSocket;
	}

	/**
	 * 创建清理无效连接线程
	 *
	 * @param serverListenThread 服务器监听线程
	 * @return 清理无效连接线程
	 */
	@Override
	public IClearInvalidSocketPartThread newClearInvalidSocketPartThread(ServerListenThread serverListenThread) {
		return this.baseConfig.newClearInvalidSocketPartThread(serverListenThread);
	}

	/**
	 * 创建SocketPart
	 *
	 * @param serverListenThread 服务器监听线程
	 * @return SocketPart
	 */
	@Override
	public BaseSocketPart newSocketPart(ServerListenThread serverListenThread) {
		return this.baseConfig.newSocketPart(serverListenThread);
	}

	/**
	 * 获取监听端口
	 *
	 * @return 监听端口
	 */
	@Override
	public Integer getListenPort() {
		return this.baseConfig.getListenPort();
	}

	/**
	 * 获取字符集
	 *
	 * @return 字符集
	 */
	@Override
	public Charset getCharset() {
		return this.baseConfig.getCharset();
	}

}
