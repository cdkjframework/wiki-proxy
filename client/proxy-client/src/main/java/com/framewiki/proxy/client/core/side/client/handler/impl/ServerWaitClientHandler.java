package com.framewiki.proxy.client.core.side.client.handler.impl;

import com.framewiki.network.proxy.model.InteractiveModel;
import com.framewiki.network.proxy.model.enums.InteractiveTypeEnum;
import com.framewiki.network.proxy.model.interactive.ServerWaitModel;
import com.framewiki.proxy.client.core.side.client.adapter.ClientAdapter;
import com.framewiki.proxy.client.core.side.client.handler.IClientHandler;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.client.core.side.client.config
 * @ClassName: InteractiveClientConfig
 * @Description: 心跳检测
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public class ServerWaitClientHandler implements IClientHandler<InteractiveModel, InteractiveModel> {

	/**
	 * 心跳检测
	 */
	public static final ServerWaitClientHandler INSTANCE = new ServerWaitClientHandler();

	/**
	 * 处理消息
	 *
	 * @param model         接收到的消息
	 * @param clientAdapter 客户端适配器
	 * @return 是否处理成功
	 * @throws Exception 处理失败
	 */
	@Override
	public boolean proc(InteractiveModel model,
											ClientAdapter<? extends InteractiveModel, ? super InteractiveModel> clientAdapter) throws Exception {
		InteractiveTypeEnum interactiveTypeEnum = InteractiveTypeEnum.getEnumByName(model.getInteractiveType());
		if (!InteractiveTypeEnum.SERVER_WAIT_CLIENT.equals(interactiveTypeEnum)) {
			return false;
		}

		ServerWaitModel serverWaitModel = model.getData().toJavaObject(ServerWaitModel.class);
		clientAdapter.clientConnect(serverWaitModel);

		return true;
	}

}
