package com.framewiki.network.proxy.executor;

import java.util.concurrent.ScheduledFuture;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.nio
 * @ClassName: IExecutor
 * @Description: 执行器实现
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public interface IExecutor {

	/**
	 * 关闭所有任务
	 *
	 * @throws Exception 抛出异常
	 */
	void shutdown();

	/**
	 * 默认执行方法
	 *
	 * @param runnable 待执行任务
	 */
	void execute(Runnable runnable);

	/**
	 * 服务监听线程任务执行器
	 *
	 * @param runnable
	 */
	default void executeServerListenAccept(Runnable runnable) {
		execute(runnable);
	}

	/**
	 * 客户端监听线程任务执行器
	 *
	 * @param runnable 待执行任务
	 */
	default void executeClientServiceAccept(Runnable runnable) {
		execute(runnable);
	}

	/**
	 * 客户端消息处理任务执行器
	 *
	 * @param runnable 待执行任务
	 */
	default void executeClientMessageProc(Runnable runnable) {
		execute(runnable);
	}

	/**
	 * 隧道线程执行器
	 * For {@link com.framewiki.network.proxy.api.passway}
	 *
	 * @param runnable 待执行任务
	 */
	default void executePassWay(Runnable runnable) {
		execute(runnable);
	}

	/**
	 * nio事件任务执行器
	 * For {@link com.framewiki.network.proxy.nio.impl.NioHallows#run()}
	 *
	 * @param runnable 待执行任务
	 */
	default void executeNioAction(Runnable runnable) {
		execute(runnable);
	}

	/**
	 * 心跳检测定时循环任务执行
	 *
	 * @param runnable     待执行任务
	 * @param delaySeconds 延迟秒数
	 * @return ScheduledFuture
	 */
	ScheduledFuture<?> scheduledClientHeart(Runnable runnable, long delaySeconds);

	/**
	 * 服务监听清理无效socket对
	 *
	 * @param runnable     待执行任务
	 * @param delaySeconds 延迟秒数
	 * @return ScheduledFuture
	 */
	ScheduledFuture<?> scheduledClearInvalidSocketPart(Runnable runnable, long delaySeconds);

}
