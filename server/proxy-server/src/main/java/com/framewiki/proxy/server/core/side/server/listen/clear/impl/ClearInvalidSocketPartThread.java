package com.framewiki.proxy.server.core.side.server.listen.clear.impl;

import com.framewiki.proxy.server.core.side.server.listen.ServerListenThread;
import com.framewiki.proxy.server.core.side.server.listen.clear.IClearInvalidSocketPartThread;
import com.framewiki.network.proxy.executor.FrameExecutor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.side.server.listen
 * @ClassName: ClearInvalidSocketPartThread
 * @Description: 清理无效端口
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
@Slf4j
public class ClearInvalidSocketPartThread implements IClearInvalidSocketPartThread {

	/**
	 * 监听线程
	 */
	private final ServerListenThread serverListenThread;

	/**
	 * 定时任务
	 */
	private ScheduledFuture<?> scheduledFuture;

	/**
	 * 清理间隔
	 */
	@Setter
	@Getter
	private long clearIntervalSeconds = 10L;

	/**
	 * 构建函数
	 *
	 * @param serverListenThread 监听线程
	 */
	public ClearInvalidSocketPartThread(ServerListenThread serverListenThread) {
		this.serverListenThread = serverListenThread;
	}

	/**
	 * 运行
	 */
	@Override
	public void run() {
		this.serverListenThread.clearInvalidSocketPart();
	}

	/**
	 * 启动
	 */
	@Override
	public synchronized void start() {
		ScheduledFuture<?> scheduledFuture = this.scheduledFuture;
		if (Objects.isNull(scheduledFuture) || scheduledFuture.isCancelled()) {
			this.scheduledFuture = FrameExecutor.scheduledClearInvalidSocketPart(this, this.clearIntervalSeconds);
		}

		log.info("ClearInvalidSocketPartThread for [{}] started !", this.serverListenThread.getListenPort());
	}

	/**
	 * 取消
	 */
	@Override
	public void cancel() {
		ScheduledFuture<?> scheduledFuture = this.scheduledFuture;
		if (Objects.nonNull(scheduledFuture) && !scheduledFuture.isCancelled()) {
			this.scheduledFuture = null;
			scheduledFuture.cancel(Boolean.FALSE);
		}

		log.info("ClearInvalidSocketPartThread for [{}] cancell !", this.serverListenThread.getListenPort());
	}

}
