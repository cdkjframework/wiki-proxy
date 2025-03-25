package com.framewiki.proxy.server.core.side.server.listen.recv.impl;

import com.framewiki.proxy.server.core.side.server.listen.recv.IRecvHandler;
import com.framewiki.network.proxy.channel.impl.BaseSocketChannel;
import com.framewiki.network.proxy.model.InteractiveModel;
import com.framewiki.network.proxy.model.enums.FrameResultEnum;
import com.framewiki.network.proxy.model.enums.InteractiveTypeEnum;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.side.server.listen.recv
 * @ClassName: ClientHeartHandler
 * @Description: 心跳检测
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public class ClientHeartHandler implements IRecvHandler<InteractiveModel, InteractiveModel> {

	/**
	 * 心跳检测处理器
	 */
	public static final ClientHeartHandler INSTANCE = new ClientHeartHandler();

	/**
	 * @param model   接收到的数据
	 * @param channel 通信通道
	 * @return 是否处理成功
	 * @throws Exception 处理失败
	 */
	@Override
	public boolean proc(InteractiveModel model,
											BaseSocketChannel<? extends InteractiveModel, ? super InteractiveModel> channel) throws Exception {
		InteractiveTypeEnum interactiveTypeEnum = InteractiveTypeEnum.getEnumByName(model.getInteractiveType());
		if (!InteractiveTypeEnum.HEART_TEST.equals(interactiveTypeEnum)) {
			return false;
		}
		InteractiveModel sendModel = InteractiveModel.of(model.getInteractiveSeq(), InteractiveTypeEnum.HEART_TEST_REPLY,
				FrameResultEnum.SUCCESS.toResultModel());
		channel.writeAndFlush(sendModel);
		return true;
	}
}
