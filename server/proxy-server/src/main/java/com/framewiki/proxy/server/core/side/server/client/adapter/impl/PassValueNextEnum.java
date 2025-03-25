package com.framewiki.proxy.server.core.side.server.client.adapter.impl;

import lombok.Getter;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.side.server.client.adapter
 * @ClassName: PassValueNextEnum
 * @Description: 传值适配器的handler回复信息
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
@Getter
public enum PassValueNextEnum {

	/**
	 * 停止并关闭
	 */
	STOP_CLOSE(Boolean.FALSE, Boolean.TRUE),
	/**
	 * 停止但不关闭
	 */
	STOP_KEEP(Boolean.FALSE, Boolean.FALSE),
	/**
	 * 继续执行，默认关闭
	 */
	NEXT(Boolean.TRUE, Boolean.TRUE),
	/**
	 * 继续执行，但不要关闭
	 */
	NEXT_KEEP(Boolean.TRUE, Boolean.FALSE);

	/**
	 * 是否继续执行
	 */
	private final boolean next;

	/**
	 * 是否关闭
	 */
	private final boolean close;

	/**
	 * 构造方法
	 */
	PassValueNextEnum(boolean next, boolean close) {
		this.next = next;
		this.close = close;
	}

}
