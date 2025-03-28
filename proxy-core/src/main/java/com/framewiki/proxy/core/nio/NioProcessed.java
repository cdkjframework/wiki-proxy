package com.framewiki.proxy.core.nio;

import java.nio.channels.SelectionKey;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.nio
 * @ClassName: NioProcessed
 * @Description: nio 执行器
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
@FunctionalInterface
public interface NioProcessed {

	/**
	 * 需要执行的方法
	 *
	 * @param key
	 * @since 2021-04-12 17:54:50
	 */
	void processed(SelectionKey key);

}
