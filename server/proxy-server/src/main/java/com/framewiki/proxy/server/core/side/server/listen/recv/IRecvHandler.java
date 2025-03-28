package com.framewiki.proxy.server.core.side.server.listen.recv;

import com.framewiki.proxy.core.channel.impl.BaseSocketChannel;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.side.server.listen.recv
 * @ClassName: IRecvHandler
 * @Description: 接收处理器
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public interface IRecvHandler<R, W> {

	/**
	 * 处理方法
	 *
	 * @param model   接收到的数据
	 * @param channel 通信通道
	 * @return 处理结果
	 * @throws Exception 异常
	 */
	boolean proc(R model, BaseSocketChannel<? extends R, ? super W> channel) throws Exception;
}
