package com.framewiki.proxy.client.core.side.client.adapter;

import com.framewiki.network.proxy.channel.impl.BaseSocketChannel;
import com.framewiki.network.proxy.model.interactive.ServerWaitModel;

import java.time.LocalDateTime;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.client.core.side.client.adapter
 * @ClassName: IClientAdapter
 * @Description: 客户端适配器
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public interface ClientAdapter<R, W> {

	/**
	 * 请求建立控制器
	 *
	 * @return
	 * @throws Exception
	 */
	boolean createControlChannel() throws Exception;

	/**
	 * 请求建立隧道连接
	 *
	 * @param serverWaitModel 服务端返回的等待模型
	 * @return
	 */
	boolean clientConnect(ServerWaitModel serverWaitModel);

	/**
	 * 等待消息处理
	 *
	 * @throws Exception 异常
	 */
	void waitMessage() throws Exception;

	/**
	 * 关闭
	 *
	 * @throws Exception 异常
	 */
	void close() throws Exception;

	/**
	 * 向控制器发送心跳
	 *
	 * @throws Exception 异常
	 */
	void sendHeartTest() throws Exception;

	/**
	 * 获取服务端最后心跳测试/回复的时间
	 *
	 * @return
	 */
	LocalDateTime obtainServerHeartLastRecvTime();

	/**
	 * 重设服务端最后心跳测试/回复时间
	 * <p>
	 * 取 {@link LocalDateTime#now()} 为 设定值
	 *
	 * @return 设置值
	 */
	default LocalDateTime resetServerHeartLastRecvTime() {
		return this.resetServerHeartLastRecvTime(LocalDateTime.now());
	}

	/**
	 * 重设服务端最后心跳测试/回复时间
	 *
	 * @param time 自有设置
	 * @return
	 */
	LocalDateTime resetServerHeartLastRecvTime(LocalDateTime time);

	/**
	 * 获取socket读写通道
	 *
	 * @return
	 */
	BaseSocketChannel<? extends R, ? super W> getSocketChannel();
}
