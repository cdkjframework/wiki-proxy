package com.framewiki.proxy.server.core.side.server.listen;

import com.cdkjframework.exceptions.GlobalRuntimeException;
import com.framewiki.proxy.server.core.side.server.listen.control.IControlSocket;
import com.framewiki.network.proxy.api.IBelongControl;
import com.framewiki.network.proxy.api.socket.part.BaseSocketPart;
import com.framewiki.network.proxy.common.CommonFormat;
import com.framewiki.network.proxy.executor.FrameExecutor;
import com.framewiki.network.proxy.nio.NioProcessed;
import com.framewiki.network.proxy.nio.impl.NioHallows;
import com.framewiki.proxy.server.core.side.server.listen.clear.IClearInvalidSocketPartThread;
import com.framewiki.proxy.server.core.side.server.listen.config.ListenServerConfig;
import com.framewiki.network.proxy.util.AssertUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.side.server.listen
 * @ClassName: ServerListenThread
 * @Description: 监听转发服务进程
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
@Slf4j
public final class ServerListenThread implements Runnable, NioProcessed, IBelongControl, ServerListen {

	/**
	 * 配置
	 */
	private final ListenServerConfig config;

	/**
	 * 监听服务
	 */
	private final ServerSocket listenServerSocket;

	/**
	 * socket map 对象
	 */
	private final ConcurrentHashMap<String, BaseSocketPart> socketPartMap = new ConcurrentHashMap<>();

	/**
	 * 线程对象
	 */
	private volatile Thread myThread = null;

	/**
	 * 线程是否存活
	 */
	private volatile boolean isAlive = false;

	/**
	 * 线程是否取消
	 */
	private volatile boolean canceled = false;

	/**
	 * 控制socket
	 */
	private volatile IControlSocket controlSocket;

	/**
	 * 清理无效
	 */
	private volatile IClearInvalidSocketPartThread clearInvalidSocketPartThread;

	/**
	 * 构造方法
	 *
	 * @param config 配置
	 * @throws Exception 异常
	 */
	public ServerListenThread(ListenServerConfig config) throws Exception {
		this.config = config;

		// 此处就开始占用端口，防止重复占用端口，和启动时已被占用
		this.listenServerSocket = this.config.createServerSocket();

		log.info("server listen port[{}] is created!", this.getListenPort());
	}

	/**
	 * 运行方法
	 */
	@Override
	public void run() {
		while (this.isAlive) {
			try {
				Socket listenSocket = this.listenServerSocket.accept();
				this.procMethod(listenSocket);
			} catch (Exception e) {
				log.warn("监听服务[" + this.getListenPort() + "]服务异常", e);
				this.cancel();
			}
		}
	}

	/**
	 * 处理方法
	 *
	 * @param key 键
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
			log.warn("监听服务[" + this.getListenPort() + "]服务异常", e);
			this.cancel();
		}
	}

	/**
	 * 任务执行方法
	 *
	 * @param listenSocket 监听socket
	 */
	private void procMethod(Socket listenSocket) {
		FrameExecutor.executeServerListenAccept(() -> {
			// 如果没有控制接收socket，则取消接入，不主动关闭所有接口，防止controlSocket临时掉线，讲道理没有controlSocket也不会启动
			if (Objects.isNull(this.controlSocket)) {
				try {
					listenSocket.close();
				} catch (IOException e) {
					// do nothing
				}
				return;
			}

			String socketPartKey = CommonFormat.generateSocketPartKey(this.getListenPort());

			BaseSocketPart socketPart = this.config.newSocketPart(this);
			socketPart.setSocketPartKey(socketPartKey);
			socketPart.setRecvSocket(listenSocket);

			this.socketPartMap.put(socketPartKey, socketPart);
			// 发送指令失败，同controlSocket为空，不使用异步执行，毕竟接口发送只能顺序，异步的方式也会被锁，等同同步
			if (!this.sendClientWait(socketPartKey)) {
				this.socketPartMap.remove(socketPartKey);
				socketPart.cancel();
			}
		});
	}

	/**
	 * 告知客户端，有新连接
	 *
	 * @param socketPartKey  隧道标识
	 */
	private boolean sendClientWait(String socketPartKey) {
		log.info("告知新连接 sendClientWait[{}]", socketPartKey);
		boolean sendClientWait;

		IControlSocket controlSocket = this.controlSocket;

		try {
			sendClientWait = controlSocket.sendClientWait(socketPartKey);
		} catch (Throwable e) {
			log.error("告知新连接 sendClientWait[" + socketPartKey + "] 发生未知异常", e);
			sendClientWait = false;
		}

		if (!sendClientWait) {
			log.warn("告知新连接 sendClientWait[" + socketPartKey + "] 失败");
			if (!controlSocket.isValid()) {
				// 保证control为置空状态
				this.stopListen();
			}
			return false;
		}
		return true;
	}

	/**
	 * 启动
	 *
	 * @throws Exception 异常
	 */
	private void start() {
		AssertUtils.state(!this.canceled, "已退出，不得重新启动");
		if (this.isAlive) {
			return;
		}
		this.isAlive = true;

		log.info("server listen port[{}] starting ...", this.getListenPort());

		if (this.clearInvalidSocketPartThread == null) {
			this.clearInvalidSocketPartThread = this.config.newClearInvalidSocketPartThread(this);
			if (this.clearInvalidSocketPartThread != null) {
				this.clearInvalidSocketPartThread.start();
			}
		}

		ServerSocketChannel channel = this.listenServerSocket.getChannel();
		if (Objects.nonNull(channel)) {
			try {
				NioHallows.register(channel, SelectionKey.OP_ACCEPT, this);
			} catch (IOException e) {
				log.error("register serverListen channel[{}] failed!", config.getListenPort());
				this.cancel();
				throw new GlobalRuntimeException("nio注册时异常", e);
			}
		} else {
			Thread myThread = this.myThread;
			if (myThread == null || !myThread.isAlive()) {
				myThread = this.myThread = new Thread(this);
				myThread.setName("server-listen-" + this.formatInfo());
				myThread.start();
			}
		}

		log.info("server listen port[{}] start success!", this.getListenPort());
	}

	/**
	 * 关停监听服务，不注销已经建立的，并置空controlSocket
	 */
	private synchronized void stopListen() {
		log.info("stopListen[{}]", this.getListenPort());
		this.isAlive = false;

		NioHallows.release(this.listenServerSocket.getChannel());

		Thread myThread;
		if ((myThread = this.myThread) != null) {
			this.myThread = null;
			myThread.interrupt();
		}

		IControlSocket controlSocket = this.controlSocket;
		if (Objects.nonNull(controlSocket)) {
			this.controlSocket = null;
			try {
				controlSocket.close();
			} catch (Exception e) {
				log.debug("监听服务控制端口关闭异常", e);
			}
		}
	}

	/**
	 * 退出
	 */
	public synchronized void cancel() {
		if (this.canceled) {
			return;
		}
		this.canceled = true;

		log.info("serverListen cancelling[{}]", this.getListenPort());

		ListenServerControl.remove(this.getListenPort());

		this.stopListen();

		try {
			this.listenServerSocket.close();
		} catch (Exception e) {
			// do nothing
		}

		IClearInvalidSocketPartThread clearInvalidSocketPartThread;
		if ((clearInvalidSocketPartThread = this.clearInvalidSocketPartThread) != null) {
			this.clearInvalidSocketPartThread = null;
			try {
				clearInvalidSocketPartThread.cancel();
			} catch (Exception e) {
				// do nothing
			}
		}

		String[] socketPartKeyArray = this.socketPartMap.keySet().toArray(new String[0]);
		for (String key : socketPartKeyArray) {
			this.stopSocketPart(key);
		}

		log.debug("serverListen cancel[{}] is success", this.getListenPort());
	}

	/**
	 * 无标记通知
	 */
	@Override
	public void noticeStop() {
		IBelongControl.super.noticeStop();
	}

	/**
	 * 停止指定的端口
	 *
	 * @param socketPartKey  隧道标识
	 * @return boolean
	 */
	@Override
	public boolean stopSocketPart(String socketPartKey) {
		log.debug("停止接口 stopSocketPart[{}]", socketPartKey);
		BaseSocketPart socketPart = this.socketPartMap.remove(socketPartKey);
		if (socketPart == null) {
			return false;
		}
		socketPart.cancel();
		return true;
	}

	/**
	 * 清理无效socketPart
	 */
	public void clearInvalidSocketPart() {
		log.debug("clearInvalidSocketPart[{}]", this.getListenPort());

		ConcurrentHashMap<String, BaseSocketPart> socketPartMap = this.socketPartMap;

		Set<String> keySet = socketPartMap.keySet();
		// 被去除的时候 set会变化而导致空值问题
		String[] array = keySet.toArray(new String[0]);

		for (String key : array) {
			BaseSocketPart socketPart = socketPartMap.get(key);
			if (socketPart != null && !socketPart.isValid()) {
				this.stopSocketPart(key);
			}
		}

	}

	/**
	 * 将接受到的连接进行设置组合
	 *
	 * @param socketPartKey  隧道标识
	 * @param sendSocket    发送连接
	 * @return boolean
	 */
	public boolean doSetPartClient(String socketPartKey, Socket sendSocket) {
		log.debug("接入接口 doSetPartClient[{}]", socketPartKey);
		BaseSocketPart socketPart = this.socketPartMap.get(socketPartKey);
		if (socketPart == null) {
			return false;
		}
		socketPart.setSendSocket(sendSocket);

		boolean createPassWay = socketPart.createPassWay();
		if (!createPassWay) {
			socketPart.cancel();
			this.stopSocketPart(socketPartKey);
			return false;
		}

		return true;
	}

	/**
	 * 设置控制端口
	 *
	 * @param socket 控制端口
	 */
	public synchronized void setControlSocket(Socket socket) {
		log.info("setControlSocket[{}]", this.getListenPort());

		IControlSocket controlSocketNew = this.config.newControlSocket(socket, null);

		IControlSocket controlSocket;
		if ((controlSocket = this.controlSocket) != null) {
			this.controlSocket = null;
			try {
				controlSocket.replaceClose();
			} catch (Exception e) {
				log.debug("监听服务控制端口关闭异常", e);
			}
		}

		controlSocketNew.setServerListen(this);
		controlSocketNew.startRecv();
		this.controlSocket = controlSocketNew;
		this.start();
	}


	@Override
	public synchronized void controlCloseNotice(IControlSocket controlSocket) {
		if (Objects.equals(controlSocket, this.controlSocket)) {
			this.stopListen();
		}
	}

	/**
	 * 获取监听端口
	 *
	 * @return Integer
	 */
	public Integer getListenPort() {
		return this.config.getListenPort();
	}

	/**
	 * 获取已建立的连接
	 *
	 * @return List<String>
	 */
	public List<String> getSocketPartList() {
		return new LinkedList<>(this.socketPartMap.keySet());
	}

	/**
	 * 获取socket对
	 *
	 * @return Map<String
	 */
	public Map<String, BaseSocketPart> getSocketPartMap() {
		return this.socketPartMap;
	}

	/**
	 * 是否激活状态
	 *
	 * @return boolean
	 */
	public boolean isAlive() {
		return this.isAlive;
	}

	/**
	 * 是否已退出
	 *
	 * @return
	 */
	public boolean isCanceled() {
		return this.canceled;
	}

	/**
	 * 获取配置
	 *
	 * @return
	 */
	public ListenServerConfig getConfig() {
		return this.config;
	}

	/**
	 * 获取信息
	 *
	 * @return String
	 */
	public String formatInfo() {
		return String.valueOf(this.getListenPort());
	}

}
