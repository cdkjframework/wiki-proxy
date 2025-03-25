package com.framewiki.proxy.server.core.side.server.client.adapter.impl;

import com.framewiki.network.proxy.channel.impl.BaseSocketChannel;
import com.framewiki.network.proxy.common.Optional;
import com.framewiki.proxy.server.core.side.server.client.adapter.ClientServiceAdapter;
import com.framewiki.proxy.server.core.side.server.client.config.IClientServiceConfig;
import com.framewiki.proxy.server.core.side.server.client.handler.PassValueHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.side.server.client.adapter
 * @ClassName: ReadAheadPassValueAdapter
 * @Description: 预读后处理适配器
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
@Slf4j
public class ReadAheadPassValueAdapter<R, W> implements ClientServiceAdapter {

	/**
	 * 客户端服务配置
	 */
	private final IClientServiceConfig<R, W> config;

	/**
	 * 处理器队列
	 */
	private List<PassValueHandler<? super R, ? extends W>> handlerList = new LinkedList<>();

	/**
	 * 构造方法
	 *
	 * @param config 客户端服务配置
	 */
	public ReadAheadPassValueAdapter(IClientServiceConfig<R, W> config) {
		this.config = config;
	}

	/**
	 * 处理方法
	 *
	 * @param listenSocket 监听socket
	 * @throws Exception 异常
	 */
	@Override
	public void procMethod(Socket listenSocket) throws Exception {
		// 建立交互通道
		BaseSocketChannel<? extends R, ? super W> baseSocketChannel;
		try {
			baseSocketChannel = this.config.newSocketChannel(listenSocket);
		} catch (Exception e) {
			log.error("创建socket通道异常", e);
			throw e;
		}

		Optional<R> optional;
		try {
			R read = baseSocketChannel.read();
			optional = Optional.of(read);
		} catch (Exception e) {
			log.error("读取数据异常", e);
			throw e;
		}

		boolean closeFlag = Boolean.TRUE;
		for (PassValueHandler<? super R, ? extends W> handler : handlerList) {
			// 按照队列进行执行
			PassValueNextEnum proc = handler.proc(baseSocketChannel, optional);

			// 只要有一个需要不关闭通道则就不得关闭
			if (!proc.isClose()) {
				closeFlag = false;
			}
			// 如果无需继续向下则退出
			if (!proc.isNext()) {
				break;
			}
		}

		if (closeFlag) {
			try {
				baseSocketChannel.closeSocket();
			} catch (IOException e) {
				log.error("关闭socket异常", e);
			}
		}

	}

	/**
	 * 添加处理器到最后一个
	 *
	 * @param handler 处理器
	 * @return ReadAheadPassValueAdapter<R, W>
	 */
	public ReadAheadPassValueAdapter<R, W> addLast(PassValueHandler<? super R, ? extends W> handler) {
		this.handlerList.add(handler);
		return this;
	}

	/**
	 * 获取处理器，可以自行更改
	 *
	 * @return List<PassValueHandler < ? super R, ? extends W>>
	 */
	public List<PassValueHandler<? super R, ? extends W>> getHandlerList() {
		return this.handlerList;
	}

	/**
	 * 设置handlerList
	 *
	 * @param handlerList 设置 handlerList
	 * @return ReadAheadPassValueAdapter<R, W>
	 */
	public ReadAheadPassValueAdapter<R, W> setHandlerList(List<PassValueHandler<? super R, ? extends W>> handlerList) {
		this.handlerList = handlerList;
		return this;
	}

}
