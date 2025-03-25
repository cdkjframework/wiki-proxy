package com.framewiki.network.proxy.nio.impl;

import com.framewiki.network.proxy.nio.NioProcessed;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.nio
 * @ClassName: ProcessedHolder
 * @Description: 执行器暂存
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
@Data
@AllArgsConstructor(staticName = "of")
public class ProcessedHolder {

	/**
	 * 待处理的通道
	 */
	private SelectableChannel channel;

	/**
	 * 感兴趣的事件
	 */
	private int interestOps;

	/**
	 * 处理器
	 */
	private NioProcessed processed;

	/**
	 * 执行事件的任务
	 *
	 * @param key 待处理的事件
	 */
	public void processed(SelectionKey key) {
		this.processed.processed(key);

		if (!NioHallows.reRegisterByKey(key, this.interestOps)) {
			NioHallows.release(this.channel);
		}
	}
}
