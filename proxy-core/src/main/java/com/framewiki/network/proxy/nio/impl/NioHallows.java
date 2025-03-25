package com.framewiki.network.proxy.nio.impl;

import com.cdkjframework.util.log.LogUtils;
import com.framewiki.network.proxy.executor.FrameExecutor;
import com.framewiki.network.proxy.model.FrameResultModel;
import com.framewiki.network.proxy.nio.NioProcessed;
import com.framewiki.network.proxy.util.AssertUtils;
import com.framewiki.network.proxy.util.CountWaitLatch;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.nio
 * @ClassName: NioHallows
 * @Description: nio 容器
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NioHallows implements Runnable {
	/**
	 * 日志
	 */
	private final LogUtils log = LogUtils.getLogger(NioHallows.class);

	/**
	 * 单例
	 */
	public static final NioHallows INSTANCE = new NioHallows();

	/**
	 * 监听器
	 */
	private final Object selectorLock = new Object();

	/**
	 * 监听器数量
	 */
	private final CountWaitLatch countWaitLatch = new CountWaitLatch();

	/**
	 * 监听动作
	 */
	private final Map<SelectableChannel, ProcessedHolder> channelProcesserMap = new ConcurrentHashMap<>();

	/**
	 * 线程
	 */
	private volatile Thread myThread = null;
	/**
	 * 线程是否存活
	 */
	private volatile boolean alive = false;

	/**
	 * 线程是否被取消
	 */
	private volatile boolean canceled = false;

	/**
	 * selector
	 */
	private volatile Selector selector;

	/**
	 * 监听超时时间，单位：秒
	 */
	@Setter
	@Getter
	private long selectTimeout = 10L;

	/**
	 * 唤醒线程休眠时间，单位：纳秒
	 */
	@Setter
	@Getter
	private long wakeupSleepNanos = 1000000L;

	/**
	 * 注册监听动作
	 * 要注意这里只拿最后的一次注册为准，即 {@code channel} 只能与一个 {@code proccesser} 动作对应
	 *
	 * @param channel   要监听的通道
	 * @param ops       依据以下值进行或运算进行最后结果设定，并且 {@code channel} 要支持相应的动作
	 *                  <p>
	 *                  - {@link SelectionKey#OP_ACCEPT}
	 *                  <p>
	 *                  - {@link SelectionKey#OP_CONNECT}
	 *                  <p>
	 *                  - {@link SelectionKey#OP_READ}
	 *                  <p>
	 *                  - {@link SelectionKey#OP_WRITE}
	 * @param processed 要执行的动作
	 * @throws IOException 监听异常
	 */
	public static void register(SelectableChannel channel, int ops, NioProcessed processed) throws IOException {
		INSTANCE.register0(channel, ops, processed);
	}

	/**
	 * 根据
	 * {@link SelectionKey} 恢复监听事件的注册
	 *
	 * @param key 原始的key
	 * @param ops 要与通过 {@link #register(SelectableChannel, int, NioProcessed)}
	 *            注册的事件统一
	 * @throws IOException 恢复注册异常
	 */
	public static boolean reRegisterByKey(SelectionKey key, int ops) {
		return INSTANCE.reRegisterByKey0(key, ops);
	}

	/**
	 * 释放注册
	 *
	 * @param channel 要释放的通道
	 */
	public static void release(SelectableChannel channel) {
		INSTANCE.release0(channel);
	}

	/**
	 * 获取 {@link #selector}
	 * 若 {@link #selector} 未有值，则会进行初始化：打开selector，并执行 {@link #start()}
	 *
	 * @return {@link #selector}
	 * @throws IOException 获取select
	 */
	public Selector getSelector() throws IOException {
		// 判空、返回逻辑，按第一次取值进行，缺点是不能判断是否已经关闭，但与 this.cancel()
		// 方法中的执行顺序来看，会先被设置为null，再去close，所以可以大概率认为若不为null即为没有关闭
		Selector selector = this.selector;
		if (Objects.isNull(selector)) {
			synchronized (this.selectorLock) {
				// 二次校验
				// 若是主动退出，则不在创建，避免退出时有新任务而被重启，若要重新启用，则需要主动调用 start() 方法来启动
				if (Objects.isNull(this.selector) && !this.canceled) {
					this.selector = Selector.open();
					this.start();
				}
			}
			selector = this.selector;
			if (Objects.isNull(selector)) {
				throw new IOException("NioHallows's selector is closed");
			}
		}

		return selector;
	}

	/**
	 * 获取唤醒后的 {@link #selector}
	 * 注意，若 {@link #run()} 快于你的任务，还是会被再次阻塞，只是执行了一次 {@link Selector#wakeup()}
	 *
	 * @return {@link #selector}
	 * @throws IOException
	 */
	public Selector getWakeupSelector() throws IOException {
		return this.getSelector().wakeup();
	}

	/**
	 * 注册监听动作
	 * 要注意这里只拿最后的一次注册为准，即 {@code channel} 只能与一个 {@code processed} 动作对应
	 *
	 * @param channel   要监听的通道
	 * @param ops       依据以下值进行或运算进行最后结果设定，并且 {@code channel} 要支持相应的动作
	 *                  <p>
	 *                  - {@link SelectionKey#OP_ACCEPT}
	 *                  <p>
	 *                  - {@link SelectionKey#OP_CONNECT}
	 *                  <p>
	 *                  - {@link SelectionKey#OP_READ}
	 *                  <p>
	 *                  - {@link SelectionKey#OP_WRITE}
	 * @param processed 要执行的动作
	 * @throws IOException 注册异常
	 */
	public void register0(SelectableChannel channel, int ops, NioProcessed processed) throws IOException {
		Objects.requireNonNull(channel, "通道非空");
		try {
			this.channelProcesserMap.put(channel, ProcessedHolder.of(channel, ops, processed));
			channel.configureBlocking(false);

			this.countWaitLatch.countUp();
			// 这里有个坑点，如果在select中，这里会被阻塞
			channel.register(this.getWakeupSelector(), ops);
		} catch (Throwable e) {
			this.channelProcesserMap.remove(channel);
			throw e;
		} finally {
			this.countWaitLatch.countDown();
		}
	}

	/**
	 * 根据 {@link SelectionKey} 恢复监听事件的注册
	 *
	 * @param key 原始的key
	 * @param ops 要与通过 {@link #register0(SelectableChannel, int, NioProcessed)}
	 *            注册的事件统一
	 * @throws IOException 恢复注册异常
	 */
	public boolean reRegisterByKey0(SelectionKey key, int ops) {
		Objects.requireNonNull(key, "通道非空");
		AssertUtils.state(key.selector() == this.selector, "this SelectionKey is not belong NioHallows's selector");
		if (!key.isValid()) {
			return false;
		}

		// 通过事件和源码分析，恢复注册是通过updateKeys.addLast进行，虽然没有被阻塞，但是需要进行一次唤醒才可以成功恢复事件监听
		// 因无法获知是否成功注入selector，所以必须要进行一次唤醒操作，并且没有阻塞的问题，所以这里不通过countWaitLatch进行同步
		key.interestOps(ops);

		try {
			this.getWakeupSelector();
		} catch (IOException e) {
			// 出错了交给其他的流程逻辑，这里只进行一次唤醒
		}

		return true;
	}

	/**
	 * 释放注册
	 *
	 * @param channel 要释放的通道
	 */
	public void release0(SelectableChannel channel) {
		if (Objects.isNull(channel)) {
			return;
		}
		this.channelProcesserMap.remove(channel);

		SelectionKey key = channel.keyFor(this.selector);

		if (Objects.nonNull(key)) {
			key.cancel();
		}
	}

	/**
	 * 运行入口
	 */
	@Override
	public void run() {
		CountWaitLatch countWaitLatch = this.countWaitLatch;
		while (this.alive) {
			// 给注册事务一个时间，如果等待时间太长（可能需要注入的太多），就跳出再去获取新事件，防止饿死
			try {
				countWaitLatch.await(this.getWakeupSleepNanos(), TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				log.warn("选择器等待寄存器超时");
			}

			Selector selector;
			try {
				selector = getSelector();

				// 采用有期限的监听，以免线程太快，没有来的及注册，就永远阻塞在那里了
				int select = selector.select(this.getSelectTimeout());
				if (select <= 0) {
					continue;
				}
			} catch (IOException e) {
				log.error("NioHallows运行异常", e);
				continue;
			}

			Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
			while (iterator.hasNext()) {
				SelectionKey key = iterator.next();
				iterator.remove();

				try {
					key.interestOps(0);
				} catch (Exception e) {
					// do nothing
				}

				ProcessedHolder processedHolder = this.channelProcesserMap.get(key.channel());
				if (Objects.isNull(processedHolder)) {
					key.cancel();
					continue;
				}

				FrameExecutor.executeNioAction(() -> processedHolder.processed(key));
			}
		}
	}

	/**
	 * 启动nio事件监听
	 *
	 */
	public synchronized void start() {

		this.canceled = false;
		this.alive = true;

		Thread myThread = this.myThread;
		if (myThread == null || !myThread.isAlive()) {
			myThread = this.myThread = new Thread(this);
			myThread.setName("nio-hallows");
			myThread.start();

			log.info("NioHallows is started!");
		}
	}

	/**
	 * Exit nio event monitoring
	 */
	public void cancel() {
		// 假设A线程执行到了 this.selector = Selector.open() 但是调用 this.cancel()
		// 方法的B线程抢占cpu成功，并一直到执行完成，此时A线程抢占CPU继续执行，又会进行重启，与关停项目时的关停期望不同。
		//
		// 此处锁定 this.selectorLock 后再去设置 this.canceled，形成与 this.getSelector()
		// 的线程同步，同时避免了被动调用 this.start() 时与 this.cancel() 的同步问题，最终可关闭。
		// 虽与主动调用 this.start() 有不同步的风险，但 this.start() 、 this.cancel()
		// 主动调用的场景有极大对立性，所以不进行过多的关照。
		//
		// 注意：若 this.cancel() 添加了synchronized，存在死锁的可能！！！
		synchronized (this.selectorLock) {
			this.canceled = true;
		}

		log.info("NioHallows cancel");

		this.alive = false;

		Selector selector;
		if ((selector = this.selector) != null) {
			this.selector = null;
			try {
				selector.close();
			} catch (IOException e) {
				// do nothing
			}
		}

		Thread myThread;
		if ((myThread = this.myThread) != null) {
			this.myThread = null;
			myThread.interrupt();
		}
	}

}
