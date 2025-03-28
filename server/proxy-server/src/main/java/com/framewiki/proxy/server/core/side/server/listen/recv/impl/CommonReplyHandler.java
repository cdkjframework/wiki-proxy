package com.framewiki.proxy.server.core.side.server.listen.recv.impl;

import com.cdkjframework.util.log.LogUtils;
import com.framewiki.proxy.core.channel.impl.BaseSocketChannel;
import com.framewiki.proxy.core.model.InteractiveModel;
import com.framewiki.proxy.core.model.enums.InteractiveTypeEnum;
import com.framewiki.proxy.server.core.side.server.listen.recv.IRecvHandler;
import lombok.Getter;
import lombok.Setter;


import java.util.Objects;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.side.server.listen.recv
 * @ClassName: CommonReplyHandler
 * @Description: 统一回复 处理器
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */

public class CommonReplyHandler implements IRecvHandler<InteractiveModel, InteractiveModel> {
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
	private IRecvHandler<InteractiveModel, InteractiveModel> handler;

	/**
	 * 处理器
	 *
	 * @param model   接收到的数据
	 * @param channel 通信通道
	 * @return 处理结果
	 * @throws Exception 处理异常
	 */
	@Override
	public boolean proc(InteractiveModel model,
											BaseSocketChannel<? extends InteractiveModel, ? super InteractiveModel> channel) throws Exception {
		InteractiveTypeEnum interactiveTypeEnum = InteractiveTypeEnum.getEnumByName(model.getInteractiveType());
		if (!InteractiveTypeEnum.COMMON_REPLY.equals(interactiveTypeEnum)) {
			return false;
		}

		IRecvHandler<InteractiveModel, InteractiveModel> handler;
		if (Objects.isNull(handler = this.handler)) {
			log.info("common reply: {}", model);
			return true;
		}

		return handler.proc(model, channel);
	}
}
