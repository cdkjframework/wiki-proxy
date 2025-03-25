package com.framewiki.network.proxy.api;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.api
 * @ClassName: IBelongControl
 * @Description: 通知上次停止的统一类，为适应不同的类型进行不同的函数封装
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public interface IBelongControl {

	/**
	 * 无标记通知
	 */
	default void noticeStop() {
		// do nothing
	}

	/**
	 * 有标记通知
	 *
	 * @param socketPartKey 隧道标识
	 * @return 是否成功
	 */
	default boolean stopSocketPart(String socketPartKey) {
		return true;
	}

}
