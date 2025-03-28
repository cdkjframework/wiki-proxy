package com.framewiki.proxy.server.core.side.server.client.handler;

import com.framewiki.proxy.server.core.side.server.client.adapter.impl.PassValueNextEnum;
import com.framewiki.proxy.core.channel.impl.BaseSocketChannel;
import com.framewiki.proxy.core.common.Optional;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.side.server.client.handler
 * @ClassName: PassValueHandler
 * @Description: 传值方式客户端是配置的处理接口
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public interface PassValueHandler<R, W> {

	/**
	 * 处理方法
	 *
	 * @param baseSocketChannel 交互通道
	 * @param optional      可以重设值
	 * @return 处理结果
	 */
	PassValueNextEnum proc(BaseSocketChannel<? extends R, ? super W> baseSocketChannel, Optional<? extends R> optional);

}
