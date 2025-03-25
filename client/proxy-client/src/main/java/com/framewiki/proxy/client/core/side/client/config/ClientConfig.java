package com.framewiki.proxy.client.core.side.client.config;

import com.framewiki.network.proxy.api.socket.part.BaseSocketPart;
import com.framewiki.network.proxy.channel.impl.BaseSocketChannel;
import com.framewiki.proxy.client.core.side.client.ClientControlThread;
import com.framewiki.proxy.client.core.side.client.adapter.ClientAdapter;
import com.framewiki.proxy.client.core.side.client.heart.IClientHeartThread;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.spi.SelectorProvider;

/**
 * @param <R> 通道读取的类型
 * @param <W> 通道写入的类型
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.client.core.side.client.config
 * @ClassName: InteractiveSimpleClientAdapter
 * @Description: 客户端配置接口
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public interface ClientConfig<R, W> {

	/**
	 * 获取服务端IP
	 *
	 * @return String
	 */
	String getClientServiceIp();

	/**
	 * 获取服务端端口
	 *
	 * @return Integer
	 */
	Integer getClientServicePort();

	/**
	 * 对应的监听端口
	 *
	 * @return Integer
	 */
	Integer getListenServerPort();

	/**
	 * 目标IP
	 *
	 * @return String
	 */
	String getDestIp();

	/**
	 * 目标端口
	 *
	 * @return Integer
	 */
	Integer getDestPort();

	/**
	 * 设置目标IP
	 *
	 * @param destIp   目标IP
	 * @param destPort 目标端口
	 */
	void setDestIpPort(String destIp, Integer destPort);

	/**
	 * 新建心跳测试线程
	 *
	 * @param clientControlThread 控制线程
	 * @return
	 */
	IClientHeartThread newClientHeartThread(ClientControlThread clientControlThread);

	/**
	 * 新建适配器
	 *
	 * @param clientControlThread 控制线程
	 * @return
	 */
	ClientAdapter<R, W> newCreateControlAdapter(ClientControlThread clientControlThread);

	/**
	 * 新建与服务端的交互线程
	 *
	 * @return
	 */
	BaseSocketChannel<? extends R, ? super W> newClientChannel();

	/**
	 * 创建新的socketPart
	 *
	 * @param clientControlThread 控制线程
	 * @return
	 */
	BaseSocketPart newSocketPart(ClientControlThread clientControlThread);

	/**
	 * 创建目标端口
	 *
	 * @return Socket
	 * @throws Exception 创建异常
	 */
	default Socket newDestSocket() throws Exception {
		java.nio.channels.SocketChannel channel = SelectorProvider.provider().openSocketChannel();
		channel.connect(new InetSocketAddress(this.getDestIp(), this.getDestPort()));
		return channel.socket();
	}
}
