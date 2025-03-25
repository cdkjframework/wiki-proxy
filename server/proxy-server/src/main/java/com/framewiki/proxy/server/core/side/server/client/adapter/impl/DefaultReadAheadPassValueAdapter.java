package com.framewiki.proxy.server.core.side.server.client.adapter.impl;

import com.framewiki.proxy.server.core.side.server.client.config.IClientServiceConfig;
import com.framewiki.network.proxy.model.InteractiveModel;
import com.framewiki.proxy.server.core.side.server.client.handler.impl.DefaultInteractiveProcessHandler;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.side.server.client.adapter
 * @ClassName: DefaultReadAheadPassValueAdapter
 * @Description: 默认的预读后处理适配器
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public class DefaultReadAheadPassValueAdapter extends ReadAheadPassValueAdapter<InteractiveModel, InteractiveModel> {

	/**
	 * 构造方法
	 *
	 * @param config 配置
	 */
	public DefaultReadAheadPassValueAdapter(IClientServiceConfig<InteractiveModel, InteractiveModel> config) {
		super(config);
		this.addLast(DefaultInteractiveProcessHandler.INSTANCE);
	}

}
