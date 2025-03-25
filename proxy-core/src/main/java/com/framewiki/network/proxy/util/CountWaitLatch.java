package com.framewiki.network.proxy.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.util
 * @ClassName: CountWaitLatch
 * @Description: 可增 计数 门闩
 * @Author: frank tiger
 * @Date: 2025/1/2 13:59
 * @Version: 1.0
 */
public class CountWaitLatch {

	/**
	 * 同步控制
	 */
	private final Sync sync;

	/**
	 * 创建一个初始计数为0的CountWaitLatch
	 */
	public CountWaitLatch() {
		this(0);
	}

	/**
	 * 创建一个初始计数为count的CountWaitLatch
	 *
	 * @param count 初始计数
	 */
	public CountWaitLatch(int count) {
		if (count < 0) {
			throw new IllegalArgumentException("count < 0");
		}
		this.sync = new Sync(count);
	}

	/**
	 * 等待释放
	 *
	 * @throws InterruptedException 线程中断异常
	 */
	public void await() throws InterruptedException {
		sync.acquireSharedInterruptibly(0);
	}

	/**
	 * 等待释放
	 *
	 * @param timeout 等待时间
	 * @param unit    时间单位
	 * @return true表示成功，false表示超时
	 * @throws InterruptedException 线程中断异常
	 */
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		return sync.tryAcquireSharedNanos(0, unit.toNanos(timeout));
	}

	/**
	 * ++count
	 */
	public void countUp() {
		sync.acquireShared(1);
	}

	/**
	 * --count >= 0
	 */
	public void countDown() {
		sync.releaseShared(1);
	}

	/**
	 * count >= 0
	 */
	public long getCount() {
		return sync.getCount();
	}

	/**
	 * toString
	 *
	 * @return String
	 */
	@Override
	public String toString() {
		return super.toString() + "[Count = " + sync.getCount() + "]";
	}

	/**
	 * CountWaitLatch的同步控制。使用AQS状态表示
	 */
	private static final class Sync extends AbstractQueuedSynchronizer {

		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = -1L;

		/**
		 * 构造函数
		 *
		 * @param count 初始计数
		 */
		Sync(int count) {
			setState(count);
		}

		/**
		 * 获取当前计数
		 *
		 * @return int
		 */
		int getCount() {
			return getState();
		}

		/**
		 * 尝试获取共享锁
		 *
		 * @param acquires 获取共享锁的请求数量
		 * @return int
		 */
		@Override
		protected int tryAcquireShared(int acquires) {
			if (acquires == 0) {
				return (getState() == 0) ? acquires : -1;
			}

			for (; ; ) {
				int c = getState();
				int next = c + acquires;
				if (compareAndSetState(c, next)) {
					return acquires;
				}
			}
		}

		/**
		 * 尝试释放共享锁
		 *
		 * @param releases 释放共享锁的请求数量
		 * @return boolean
		 */
		@Override
		protected boolean tryReleaseShared(int releases) {
			// Decrement count; signal when transition to zero
			for (; ; ) {
				int c = getState();
				if (c == 0) {
					return false;
				}
				int next = c - releases;
				if (compareAndSetState(c, next)) {
					return next == 0;
				}
			}
		}
	}
}
