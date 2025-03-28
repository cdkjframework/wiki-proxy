package com.framewiki.proxy.client.core.side.client.handler.impl;

import com.cdkjframework.util.log.LogUtils;
import com.framewiki.proxy.core.model.InteractiveModel;
import com.framewiki.proxy.core.model.enums.FrameResultEnum;
import com.framewiki.proxy.core.model.enums.InteractiveTypeEnum;
import com.framewiki.proxy.client.core.side.client.adapter.ClientAdapter;
import com.framewiki.proxy.client.core.side.client.handler.IClientHandler;


/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.client.core.side.client.handler
 * @ClassName: ServerHeartHandler
 * @Description: 心跳检测
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */

public class ServerHeartHandler implements IClientHandler<InteractiveModel, InteractiveModel> {
	/**
	 * 日志
	 */
	private final LogUtils log = LogUtils.getLogger(ServerHeartHandler.class);

	/**
	 * 心跳检测
	 */
	public static final ServerHeartHandler INSTANCE = new ServerHeartHandler();

	/**
	 * 处理消息
	 *
	 * @param model         接收到的消息
	 * @param clientAdapter 客户端适配器
	 * @return 处理结果
	 * @throws Exception 处理异常
	 */
	@Override
	public boolean proc(InteractiveModel model,
											ClientAdapter<? extends InteractiveModel, ? super InteractiveModel> clientAdapter) throws Exception {
		InteractiveTypeEnum interactiveTypeEnum = InteractiveTypeEnum.getEnumByName(model.getInteractiveType());
		if (InteractiveTypeEnum.HEART_TEST.equals(interactiveTypeEnum)) {
			InteractiveModel sendModel = InteractiveModel.of(model.getInteractiveSeq(), InteractiveTypeEnum.HEART_TEST_REPLY,
					FrameResultEnum.SUCCESS.toResultModel());
			clientAdapter.getSocketChannel().writeAndFlush(sendModel);

			return true;
		} else if (InteractiveTypeEnum.HEART_TEST_REPLY.equals(interactiveTypeEnum)) {
			log.debug("HEART_TEST_REPLY ignore");
			return true;
		}
		return false;
	}
}
