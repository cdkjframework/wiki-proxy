package com.framewiki.proxy.server.core.side.server.client.process.impl;

import com.framewiki.proxy.server.core.side.server.listen.ListenServerControl;
import com.framewiki.proxy.server.core.side.server.listen.ServerListenThread;
import com.framewiki.proxy.core.channel.impl.BaseSocketChannel;
import com.framewiki.proxy.core.model.FrameResultModel;
import com.framewiki.proxy.core.model.InteractiveModel;
import com.framewiki.proxy.core.model.enums.FrameResultEnum;
import com.framewiki.proxy.core.model.enums.InteractiveTypeEnum;
import com.framewiki.proxy.core.model.interactive.ClientControlModel;
import com.framewiki.proxy.server.core.side.server.client.process.IProcess;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.side.server.client.process
 * @ClassName: ClientControlProcess
 * @Description: 请求建立控制器处理方法
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public class ClientControlProcess implements IProcess {

	/**
	 * 单例
	 */
	public static final ClientControlProcess INSTANCE = new ClientControlProcess();

	/**
	 * 判断是否处理
	 *
	 * @param interactiveModel 接收到的交互信息
	 * @return 是否处理
	 */
	@Override
	public boolean wouldProc(InteractiveModel interactiveModel) {
		InteractiveTypeEnum interactiveTypeEnum = InteractiveTypeEnum.getEnumByName(
				interactiveModel.getInteractiveType());
		return InteractiveTypeEnum.CLIENT_CONTROL.equals(interactiveTypeEnum);
	}

	/**
	 * 处理方法
	 *
	 * @param baseSocketChannel    通信通道
	 * @param interactiveModel 接收到的交互信息
	 * @return 处理结果
	 * @throws Exception 处理过程中发生的异常
	 */
	@Override
	public boolean processMethod(BaseSocketChannel<? extends InteractiveModel, ? super InteractiveModel> baseSocketChannel,
															 InteractiveModel interactiveModel) throws Exception {
		ClientControlModel clientControlModel = interactiveModel.getData().toJavaObject(ClientControlModel.class);
		ServerListenThread serverListenThread = ListenServerControl.get(clientControlModel.getListenPort());

		if (serverListenThread == null) {
			baseSocketChannel.writeAndFlush(
					InteractiveModel.of(interactiveModel.getInteractiveSeq(), InteractiveTypeEnum.COMMON_REPLY,
							FrameResultEnum.NO_HAS_SERVER_LISTEN.toResultModel()));
			return Boolean.FALSE;
		}
		baseSocketChannel.writeAndFlush(
				InteractiveModel.of(interactiveModel.getInteractiveSeq(), InteractiveTypeEnum.COMMON_REPLY,
						FrameResultModel.ofSuccess()));
		serverListenThread.setControlSocket(baseSocketChannel.getSocket());
		return Boolean.TRUE;
	}

}
