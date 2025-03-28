package com.framewiki.proxy.core.executor.impl;

import com.framewiki.proxy.core.executor.IExecutor;

import java.util.concurrent.*;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.executor.impl
 * @ClassName: FrameExecutor
 * @Description: 线程执行器
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public class SimpleExecutor implements IExecutor {

	/**
	 * 线程池
	 */
	private ExecutorService executor = Executors.newCachedThreadPool();

	/**
	 * 定时线程池
	 */
	private ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

	/**
	 * 关闭
	 */
	@Override
	public void shutdown() {
		this.executor.shutdownNow();
		this.scheduledExecutor.shutdownNow();
	}

	/**
	 * 执行
	 *
	 * @param runnable 任务
	 */
	@Override
	public void execute(Runnable runnable) {
		this.executor.execute(runnable);
	}

	/**
	 * 定时执行
	 *
	 * @param runnable     任务
	 * @param delaySeconds 延迟时间
	 * @return 定时任务
	 */
	@Override
	public ScheduledFuture<?> scheduledClientHeart(Runnable runnable, long delaySeconds) {
		return this.scheduledExecutor.scheduleWithFixedDelay(runnable, delaySeconds, delaySeconds, TimeUnit.SECONDS);
	}

	/**
	 * 定时清理
	 *
	 * @param runnable     清理任务
	 * @param delaySeconds 延迟时间
	 * @return 清理任务
	 */
	@Override
	public ScheduledFuture<?> scheduledClearInvalidSocketPart(Runnable runnable, long delaySeconds) {
		return this.scheduledExecutor.scheduleWithFixedDelay(runnable, delaySeconds, delaySeconds, TimeUnit.SECONDS);
	}

}
