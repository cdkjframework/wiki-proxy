package com.framewiki.proxy.client.core.side.client.heart;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.client.core.side.client.heart
 * @ClassName: IClientHeartThread
 * @Description: 心跳测试线程
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public interface IClientHeartThread {

	/**
	 * 是否还活着
	 *
	 * @return boolean
	 */
    boolean isAlive();

    /**
     * 退出
     */
    void cancel();

    /**
     * 开始
     */
    void start();

}
