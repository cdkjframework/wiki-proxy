package com.framewiki.proxy.server.core.side.server.listen.config;

import com.alibaba.fastjson.JSONObject;
import com.framewiki.proxy.server.core.side.server.listen.ServerListenThread;
import com.framewiki.proxy.server.core.side.server.listen.clear.IClearInvalidSocketPartThread;
import com.framewiki.proxy.server.core.side.server.listen.control.IControlSocket;
import com.framewiki.network.proxy.api.socket.part.BaseSocketPart;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.side.server.listen.config
 * @ClassName: ListenServerConfig
 * @Description: 穿透监听服务配置
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public interface ListenServerConfig {

	/**
	 * 获取监听的端口
	 *
	 * @return int
	 */
	Integer getListenPort();

	/**
	 * 新建无效端口处理线程
	 *
	 * @param serverListenThread 服务器监听线程
	 * @return
	 */
	IClearInvalidSocketPartThread newClearInvalidSocketPartThread(ServerListenThread serverListenThread);

	/**
	 * 创建隧道伙伴
	 *
	 * @param serverListenThread 服务器监听线程
	 * @return
	 */
	BaseSocketPart newSocketPart(ServerListenThread serverListenThread);

	/**
	 * 获取字符集
	 *
	 * @return Charset
	 */
	Charset getCharset();

	/**
	 * 新建控制器
	 *
	 * @param socket TCP连接
	 * @param config 配置
	 * @return
	 */
	IControlSocket newControlSocket(Socket socket, JSONObject config);

	/**
	 * 创建监听端口
	 *
	 * @return 服务端
	 * @throws Exception 创建失败
	 */
	ServerSocket createServerSocket() throws Exception;
}
