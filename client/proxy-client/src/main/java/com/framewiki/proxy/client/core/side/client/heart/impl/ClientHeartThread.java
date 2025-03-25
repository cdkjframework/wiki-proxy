package com.framewiki.proxy.client.core.side.client.heart.impl;

import com.framewiki.network.proxy.executor.FrameExecutor;
import com.framewiki.proxy.client.core.side.client.ClientControlThread;
import com.framewiki.proxy.client.core.side.client.heart.IClientHeartThread;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.client.core.side.client.heart
 * @ClassName: ClientHeartThread
 * @Description: 心跳检测线程
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
@Slf4j
public class ClientHeartThread implements IClientHeartThread, Runnable {

	/**
	 * 客户端控制线程
	 */
	private final ClientControlThread clientControlThread;

	/**
	 * 是否存活
	 */
	private volatile boolean isAlive = false;

	/**
	 * 心跳间隔时间
	 */
	@Setter
	@Getter
	private long heartIntervalSeconds = 10L;
	/**
	 * 重试次数
	 */
	@Setter
	@Getter
	private int tryRecipientCount = 10;

	/**
	 * 服务端
	 */
	@Setter
	@Getter
	private long serverHeartMaxMissDurationSeconds = 25L;

	/**
	 * 定时任务
	 */
	private volatile ScheduledFuture<?> scheduledFuture;

	/**
	 * 失败次数
	 */
	private int failCount = 0;

	/**
	 * 构造器
	 *
	 * @param clientControlThread 客户端控制线程
	 */
	public ClientHeartThread(ClientControlThread clientControlThread) {
		this.clientControlThread = clientControlThread;
	}

	/**
	 * 检查服务端是否心跳超时
	 *
	 * @param clientControlThread 客户端控制线程
	 * @param timeoutSeconds      服务端心跳超时时间
	 * @return boolean
	 */
	private boolean lastServerHeartEffective(ClientControlThread clientControlThread, Long timeoutSeconds) {
		return Duration.between(clientControlThread.obtainServerHeartLastRecvTime(), LocalDateTime.now())
				.get(ChronoUnit.SECONDS) < timeoutSeconds;
	}

	/**
	 * 运行
	 */
	@Override
	public void run() {
		ClientControlThread clientControlThread = this.clientControlThread;
		if (clientControlThread.isCancelled() || !this.isAlive()) {
			this.cancel();
		}

		log.debug("将客户端心脏数据发送到 {}", clientControlThread.getListenServerPort());
		try {
			// 如果服务端心跳超时则直接判定为失败，否则进行心跳检查
			if (this.lastServerHeartEffective(clientControlThread, this.serverHeartMaxMissDurationSeconds)) {
				clientControlThread.sendHeartTest();
				this.failCount = 0;

				return;
			}
		} catch (Exception e) {
			log.warn("{} 心跳异常", clientControlThread.getListenServerPort());
			clientControlThread.stopClient();
		}
		if (!this.isAlive) {
			return;
		}

		this.failCount++;

		boolean createControl = Boolean.FALSE, logFlag = Boolean.TRUE;
		try {
			createControl = clientControlThread.createControl();
		} catch (Exception reClientException) {
			log.warn("重新建立连接" + clientControlThread.getListenServerPort() + "失败第 " + this.failCount + " 次",
					reClientException);
			logFlag = false;
		}

		if (createControl) {
			log.info("重新建立连接 {} 成功，在第 {} 次", clientControlThread.getListenServerPort(), this.failCount);

			this.failCount = 0;
			return;
		}

		if (logFlag) {
			log.warn("重新建立连接" + clientControlThread.getListenServerPort() + "失败第 " + this.failCount + " 次");
		}

		if (this.failCount >= this.tryRecipientCount) {
			log.error("尝试重新连接 {} 超过最大次数，关闭客户端", clientControlThread.getListenServerPort());
			clientControlThread.cancell();
			this.cancel();
		}
	}

	/**
	 * 启动
	 */
	@Override
	public synchronized void start() {
		this.isAlive = true;

		ScheduledFuture<?> scheduledFuture = this.scheduledFuture;
		if (Objects.isNull(scheduledFuture) || scheduledFuture.isCancelled()) {
			this.failCount = 0;
			this.scheduledFuture = FrameExecutor.scheduledClientHeart(this, this.heartIntervalSeconds);
		}
	}

	/**
	 * 启动
	 */
	@Override
	public void cancel() {
		if (!this.isAlive) {
			return;
		}
		this.isAlive = false;

		ScheduledFuture<?> scheduledFuture = this.scheduledFuture;
		if (Objects.nonNull(scheduledFuture) && !scheduledFuture.isCancelled()) {
			this.scheduledFuture = null;
			scheduledFuture.cancel(false);
		}
	}

	/**
	 * 是否存活
	 *
	 * @return boolean
	 */
	@Override
	public boolean isAlive() {
		return this.isAlive;
	}

}
