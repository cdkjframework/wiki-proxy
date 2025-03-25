package com.framewiki.proxy.client.core.side.client.handler.impl;

import com.cdkjframework.util.log.LogUtils;
import com.framewiki.network.proxy.model.InteractiveModel;
import com.framewiki.network.proxy.model.enums.InteractiveTypeEnum;
import com.framewiki.proxy.client.core.side.client.adapter.ClientAdapter;
import com.framewiki.proxy.client.core.side.client.adapter.impl.InteractiveSimpleClientAdapter;
import com.framewiki.proxy.client.core.side.client.handler.IClientHandler;
import lombok.Getter;
import lombok.Setter;


import java.util.Objects;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.client.core.side.client.handler
 * @ClassName: CommonReplyHandler
 * @Description: 统一回复 处理器
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */

public class CommonReplyHandler implements IClientHandler<InteractiveModel, InteractiveModel> {

	/**
	 * 日志
	 */
	private final LogUtils log = LogUtils.getLogger(CommonReplyHandler.class);

	/**
	 * 单例
	 */
	public static final CommonReplyHandler INSTANCE = new CommonReplyHandler();

	/**
	 * 处理器
	 */
	@Getter
	@Setter
	private IClientHandler<InteractiveModel, InteractiveModel> handler;

	/**
	 * 处理
	 *
	 * @param model         接收到的数据
	 * @param clientAdapter 客户端适配器
	 * @return 处理结果
	 * @throws Exception 异常
	 */
	@Override
	public boolean proc(InteractiveModel model,
											ClientAdapter<? extends InteractiveModel, ? super InteractiveModel> clientAdapter) throws Exception {
		InteractiveTypeEnum interactiveTypeEnum = InteractiveTypeEnum.getEnumByName(model.getInteractiveType());
		if (!InteractiveTypeEnum.COMMON_REPLY.equals(interactiveTypeEnum)) {
			return false;
		}

		IClientHandler<InteractiveModel, InteractiveModel> handler;
		if (Objects.isNull(handler = this.handler)) {
			log.info("common reply: {}", model);
			return true;
		}

		return handler.proc(model, clientAdapter);
	}
}
