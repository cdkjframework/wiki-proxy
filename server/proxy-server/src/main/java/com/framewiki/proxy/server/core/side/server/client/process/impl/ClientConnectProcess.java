package com.framewiki.proxy.server.core.side.server.client.process.impl;

import com.framewiki.proxy.server.core.side.server.client.process.IProcess;
import com.framewiki.proxy.server.core.side.server.listen.ListenServerControl;
import com.framewiki.proxy.server.core.side.server.listen.ServerListenThread;
import com.framewiki.network.proxy.channel.impl.BaseSocketChannel;
import com.framewiki.network.proxy.common.CommonFormat;
import com.framewiki.network.proxy.model.FrameResultModel;
import com.framewiki.network.proxy.model.InteractiveModel;
import com.framewiki.network.proxy.model.enums.FrameResultEnum;
import com.framewiki.network.proxy.model.enums.InteractiveTypeEnum;
import com.framewiki.network.proxy.model.interactive.ClientConnectModel;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.side.server.client.process
 * @ClassName: ClientConnectProcess
 * @Description: 请求建立隧道处理器
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public class ClientConnectProcess implements IProcess {

	/**
	 * 单例
	 */
	public static final ClientConnectProcess INSTANCE = new ClientConnectProcess();

	/**
	 * 判断是否需要处理
	 *
	 * @param interactiveModel 接收到的交互信息
	 * @return 是否需要处理
	 */
	@Override
	public boolean wouldProc(InteractiveModel interactiveModel) {
		InteractiveTypeEnum interactiveTypeEnum = InteractiveTypeEnum.getEnumByName(
				interactiveModel.getInteractiveType());
		return InteractiveTypeEnum.CLIENT_CONNECT.equals(interactiveTypeEnum);
	}

	/**
	 * 处理
	 *
	 * @param baseSocketChannel    通信通道
	 * @param interactiveModel 接收到的交互信息
	 * @return 处理结果
	 * @throws Exception 处理异常
	 */
	@Override
	public boolean processMethod(BaseSocketChannel<? extends InteractiveModel, ? super InteractiveModel> baseSocketChannel,
															 InteractiveModel interactiveModel) throws Exception {
		ClientConnectModel clientConnectModel = interactiveModel.getData().toJavaObject(ClientConnectModel.class);
		Integer listenPort = CommonFormat.getSocketPortByPartKey(clientConnectModel.getSocketPartKey());

		ServerListenThread serverListenThread = ListenServerControl.get(listenPort);

		if (serverListenThread == null) {
			baseSocketChannel.writeAndFlush(
					InteractiveModel.of(interactiveModel.getInteractiveSeq(), InteractiveTypeEnum.COMMON_REPLY,
							FrameResultEnum.NO_HAS_SERVER_LISTEN.toResultModel()));
			return Boolean.FALSE;
		}
		// 回复设置成功，如果doSetPartClient没有找到对应的搭档，则直接按关闭处理
		baseSocketChannel.writeAndFlush(
				InteractiveModel.of(interactiveModel.getInteractiveSeq(), InteractiveTypeEnum.COMMON_REPLY,
						FrameResultModel.ofSuccess()));

		// 若设置成功，则上层无需关闭 若设置失败，则由上层关闭
		return serverListenThread.doSetPartClient(clientConnectModel.getSocketPartKey(), baseSocketChannel.getSocket());
	}

}
