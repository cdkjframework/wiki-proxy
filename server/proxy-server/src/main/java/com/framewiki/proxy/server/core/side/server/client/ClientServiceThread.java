package com.framewiki.proxy.server.core.side.server.client;

import com.cdkjframework.exceptions.GlobalRuntimeException;
import com.cdkjframework.util.log.LogUtils;
import com.framewiki.proxy.server.core.side.server.client.config.IClientServiceConfig;
import com.framewiki.network.proxy.executor.FrameExecutor;
import com.framewiki.network.proxy.nio.NioProcessed;
import com.framewiki.network.proxy.nio.impl.NioHallows;
import com.framewiki.network.proxy.util.AssertUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Objects;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.side.server.client
 * @ClassName: ClientServiceThread
 * @Description: Client service process
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public final class ClientServiceThread implements Runnable, NioProcessed {

	/**
	 * Listening socket
	 */
	private final ServerSocket listenServerSocket;
	/**
	 * configuration
	 */
	private final IClientServiceConfig<?, ?> config;

	/**
	 * log
	 */
	private LogUtils log = LogUtils.getLogger(ClientServiceThread.class);

	/**
	 * Thread
	 */
	private volatile Thread myThread = null;

	/**
	 * Is it activated
	 */
	private volatile boolean isAlive = false;

	/**
	 * Has it exited
	 */
	private volatile boolean canceled = false;

	/**
	 * constructor
	 *
	 * @param config configuration
	 * @throws Exception abnormal information
	 */
	public ClientServiceThread(IClientServiceConfig<?, ?> config) throws Exception {
		this.config = config;

		// 启动时配置，若启动失败则执行cancell并再次抛出异常让上级处理
		this.listenServerSocket = config.createServerSocket();

		log.info("client service [{}] is created!", this.config.getListenPort());
	}

	/**
	 * run
	 */
	@Override
	public void run() {
		while (this.isAlive) {
			try {
				Socket listenSocket = this.listenServerSocket.accept();
				this.procMethod(listenSocket);
			} catch (Exception e) {
				log.error("The client service process encountered an exception while waiting for polling", e);
				this.cancel();
			}
		}
	}

	/**
	 * Methods to be executed
	 *
	 * @param key Selection Key
	 */
	@Override
	public void processed(SelectionKey key) {
		if (!key.isValid()) {
			this.cancel();
		}

		try {
			ServerSocketChannel channel = (ServerSocketChannel) key.channel();
			SocketChannel accept = channel.accept();
			for (; Objects.nonNull(accept); accept = channel.accept()) {
				this.procMethod(accept.socket());
			}
		} catch (IOException e) {
			log.error("The client service process encountered an exception while waiting for polling", e);
			this.cancel();
		}
	}

	/**
	 * Process messages sent by the client
	 *
	 * @param listenSocket Listening socket
	 */
	public void procMethod(Socket listenSocket) {
		FrameExecutor.executeClientServiceAccept(() -> {
			try {
				this.config.getClientServiceAdapter().procMethod(listenSocket);
			} catch (Exception e) {
				log.error("Handling socket exceptions！", e);
				try {
					listenSocket.close();
				} catch (IOException sce) {
					log.error("Exception when handling new socket and exception when closing socket！", e);
				}
			}
		});
	}

	/**
	 * start
	 */
	public synchronized void start() {
		AssertUtils.state(!this.canceled, "Exited, cannot restart");

		log.info("customer service [{}] start ...", this.config.getListenPort());
		this.isAlive = true;

		ServerSocketChannel channel = this.listenServerSocket.getChannel();
		if (Objects.nonNull(channel)) {
			try {
				NioHallows.register(channel, SelectionKey.OP_ACCEPT, this);
			} catch (IOException e) {
				log.error("Register client service channel [{}] fail!", config.getListenPort());
				this.cancel();
				throw new GlobalRuntimeException("nio Exception during registration", e);
			}
		} else {
			if (this.myThread == null || !this.myThread.isAlive()) {
				this.myThread = new Thread(this);
				this.myThread.setName("client-server-" + this.formatInfo());
				this.myThread.start();
			}
		}

		log.info("Customer service [{}] started successfully", this.config.getListenPort());
	}

	/**
	 * exit
	 */
	public synchronized void cancel() {
		if (this.canceled) {
			return;
		}
		this.canceled = true;

		log.info("Customer service [{}] will be cancelled！", this.config.getListenPort());

		this.isAlive = false;

		ServerSocket listenServerSocket;
		if ((listenServerSocket = this.listenServerSocket) != null) {
			NioHallows.release(listenServerSocket.getChannel());
			try {
				listenServerSocket.close();
			} catch (IOException e) {
				log.error("Abnormal closure of listening port！", e);
			}
		}

		Thread myThread;
		if ((myThread = this.myThread) != null) {
			myThread.interrupt();
		}

		log.info("Customer service [{}] cancellation successful！", this.config.getListenPort());
	}

	/**
	 * Is it activated
	 *
	 * @return boolean
	 */
	public boolean isAlive() {
		return this.isAlive;
	}

	/**
	 * Has it exited
	 *
	 * @return boolean
	 */
	public boolean isCanceled() {
		return this.canceled;
	}

	/**
	 *
	 * Get listening port
	 *
	 * @return int
	 */
	public Integer getListenPort() {
		return this.config.getListenPort();
	}

	/**
	 *	 Format as short and recognizable information
	 *
	 * @return String
	 */
	public String formatInfo() {
		return String.valueOf(this.getListenPort());
	}

}
