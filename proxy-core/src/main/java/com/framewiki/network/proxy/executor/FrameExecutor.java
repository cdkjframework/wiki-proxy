package com.framewiki.network.proxy.executor;

import com.framewiki.network.proxy.executor.impl.SimpleExecutor;
import com.framewiki.network.proxy.nio.impl.NioHallows;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.nio
 * @ClassName: FrameExecutor
 * @Description:  线程执行器 主要是为了统一位置，方便管理
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FrameExecutor {

	/**
	 * 线程执行器
	 */
	private static volatile IExecutor INSTANCE = new SimpleExecutor();

	/**
	 * 关闭线程执行器
	 */
	public static void shutdown() {
		INSTANCE.shutdown();
	}

	/**
	 * 重设执行器
	 * <p>
	 * 会将旧的执行器进行执行 {@link IExecutor#shutdown()} 方法，建议重设执行器的操作在初始化程序时
	 *
	 * @param IExecutor 线程执行器
	 */
	public static void resetExecutor(IExecutor IExecutor) {
		IExecutor oldiexecutor = INSTANCE;
		if (Objects.nonNull(oldiexecutor)) {
			try {
				oldiexecutor.shutdown();
			} catch (Exception e) {
				//
			}
		}
		INSTANCE = IExecutor;
	}

	/**
	 * 服务监听线程任务执行器
	 *
	 * @param runnable 可运行
	 */
	public static void executeServerListenAccept(Runnable runnable) {
		INSTANCE.executeServerListenAccept(runnable);
	}

	/**
	 * 客户端监听线程任务执行器
	 *
	 * @param runnable 可运行
	 */
	public static void executeClientServiceAccept(Runnable runnable) {
		INSTANCE.executeClientServiceAccept(runnable);
	}

	/**
	 * 客户端消息处理任务执行器
	 *
	 * @param runnable 可运行
	 */
	public static void executeClientMessageProc(Runnable runnable) {
		INSTANCE.executeClientMessageProc(runnable);
	}

	/**
	 * 隧道线程执行器
	 * <p>
	 * For {@link com.framewiki.network.proxy.api.passway}
	 *
	 * @param runnable 可运行
	 */
	public static void executePassway(Runnable runnable) {
		INSTANCE.executePassWay(runnable);
	}

	/**
	 * nio事件任务执行器
	 * <p>
	 * For {@link NioHallows#run()}
	 *
	 * @param runnable 可运行
	 */
	public static void executeNioAction(Runnable runnable) {
		INSTANCE.executeNioAction(runnable);
	}

	/**
	 * 心跳检测定时循环任务执行
	 *
	 * @param runnable     可运行
	 * @param delaySeconds 延迟秒数
	 */
	public static ScheduledFuture<?> scheduledClientHeart(Runnable runnable, long delaySeconds) {
		return INSTANCE.scheduledClientHeart(runnable, delaySeconds);
	}

	/**
	 * 服务监听清理无效socket对
	 *
	 * @param runnable     可运行
	 * @param delaySeconds 延迟秒数
	 * @return {@link ScheduledFuture}
	 */
	public static ScheduledFuture<?> scheduledClearInvalidSocketPart(Runnable runnable, long delaySeconds) {
		return INSTANCE.scheduledClearInvalidSocketPart(runnable, delaySeconds);
	}

}
