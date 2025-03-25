package com.framewiki.proxy.server.core.side.server.client.process;

import com.framewiki.network.proxy.channel.impl.BaseSocketChannel;
import com.framewiki.network.proxy.model.InteractiveModel;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.side.server.client.process
 * @ClassName: IProcess
 * @Description: 处理方法接口
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public interface IProcess {

	/**
	 * 判断是否是由这个处理
	 *
	 * @param interactiveModel 接收到的交互信息
	 * @return 是否由这个处理
	 */
	boolean wouldProc(InteractiveModel interactiveModel);

	/**
	 * 处理方法，需要回复信息的，自己使用socketChannel回复
	 *
	 * @param baseSocketChannel    通信通道
	 * @param interactiveModel 接收到的交互信息
	 * @return 是否保持socket开启状态
	 * @throws Exception 处理过程中可能抛出的异常
	 */
	boolean processMethod(BaseSocketChannel<? extends InteractiveModel, ? super InteractiveModel> baseSocketChannel,
												InteractiveModel interactiveModel) throws Exception;

}
