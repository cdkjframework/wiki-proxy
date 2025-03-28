package com.framewiki.proxy.server.core.side.server.client.handler.impl;

import com.framewiki.proxy.server.core.side.server.client.process.impl.ClientConnectProcess;
import com.framewiki.proxy.server.core.side.server.client.process.impl.ClientControlProcess;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.side.server.client.handler
 * @ClassName: DefaultInteractiveProcessHandler
 * @Description: 默认的接收处理handler
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public class DefaultInteractiveProcessHandler extends InteractiveProcessHandler {

	/**
	 * 默认的接收处理handler
	 */
	public static final DefaultInteractiveProcessHandler INSTANCE = new DefaultInteractiveProcessHandler();

	/**
	 * 构造函数
	 */
	public DefaultInteractiveProcessHandler() {
		this.addLast(ClientControlProcess.INSTANCE);
		this.addLast(ClientConnectProcess.INSTANCE);
	}

}
