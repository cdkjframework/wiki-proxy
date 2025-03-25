package com.framewiki.proxy.server.core.side.server.listen.control.impl;

import com.framewiki.proxy.server.core.side.server.listen.control.IControlSocket;
import com.framewiki.proxy.server.core.side.server.listen.ServerListen;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.side.server.listen.config
 * @ClassName: MultiControlSocket
 * @Description: 复合 控制socket实例
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public class MultiControlSocket implements IControlSocket {

	/**
	 * 控制socket
	 */
	protected final LinkedList<IControlSocket> controlSockets = new LinkedList<>();

	/**
	 * 设置服务端监听
	 */
	protected ServerListen serverListen;

	/**
	 * 增加控制socket
	 *
	 * @param controlSocket
	 * @return
	 */
	public synchronized boolean addControlSocket(IControlSocket controlSocket) {
		return this.controlSockets.add(controlSocket);
	}

	/**
	 * 是否有效
	 *
	 * @return
	 */
	@Override
	public synchronized boolean isValid() {
		Iterator<IControlSocket> iterator = controlSockets.iterator();
		while (iterator.hasNext()) {
			IControlSocket controlSocket = iterator.next();
			if (controlSocket.isValid()) {
				return true;
			}
			iterator.remove();
		}
		return false;
	}

	/**
	 * 轮询控制socket
	 *
	 * @param lastControlSocket 上一次轮询的socket
	 * @return
	 */
	protected synchronized IControlSocket pollControlSocket(IControlSocket lastControlSocket) {
		LinkedList<IControlSocket> controlSockets = this.controlSockets;

		if (Objects.nonNull(lastControlSocket) && !lastControlSocket.isValid()) {
			lastControlSocket.close();
			controlSockets.remove(lastControlSocket);
		}

		IControlSocket controlSocket;
		if (controlSockets.size() > 1) {
			controlSocket = controlSockets.poll();
			controlSockets.add(controlSocket);
		} else {
			controlSocket = controlSockets.peekFirst();
		}

		return controlSocket;
	}

	/**
	 * 发送客户端等待消息
	 *
	 * @param socketPartKey 隧道标识
	 * @return
	 */
	@Override
	public boolean sendClientWait(String socketPartKey) {
		IControlSocket controlSocket = null;
		for (; ; ) {
			controlSocket = this.pollControlSocket(controlSocket);
			if (Objects.isNull(controlSocket)) {
				return false;
			}

			boolean sendClientWait = controlSocket.sendClientWait(socketPartKey);
			if (sendClientWait) {
				return true;
			}
		}
	}

	/**
	 * 关闭
	 */
	@Override
	public synchronized void close() {
		LinkedList<IControlSocket> controlSockets = this.controlSockets;

		Iterator<IControlSocket> iterator = controlSockets.iterator();
		while (iterator.hasNext()) {
			IControlSocket controlSocket = iterator.next();
			try {
				controlSocket.close();
			} catch (Throwable e) {
				// do nothing
			}
			iterator.remove();
		}
	}

	/**
	 * 替换关闭
	 */
	@Override
	public synchronized void replaceClose() {
		// do nothing
	}

	/**
	 * 开始接收
	 */
	@Override
	public synchronized void startRecv() {
		LinkedList<IControlSocket> controlSockets = this.controlSockets;

		for (IControlSocket controlSocket : controlSockets) {
			try {
				controlSocket.startRecv();
			} catch (Throwable e) {
				// do nothing
			}
		}
	}

	/**
	 * 移除控制socket
	 *
	 * @param controlSocket 控制socket
	 * @return 是否移除
	 */
	private synchronized boolean removeControlSocket(IControlSocket controlSocket) {
		return controlSockets.remove(controlSocket);
	}

	/**
	 * 设置服务端监听
	 *
	 * @param serverListen 服务端监听
	 */
	@Override
	public synchronized void setServerListen(final ServerListen serverListen) {
		this.serverListen = serverListen;
		ServerListen serverListenTemp = new ServerListen() {
			@Override
			public String formatInfo() {
				return serverListen.formatInfo();
			}

			@Override
			public void controlCloseNotice(IControlSocket controlSocket) {
				MultiControlSocket.this.removeControlSocket(controlSocket);
			}
		};

		LinkedList<IControlSocket> controlSockets = this.controlSockets;

		for (IControlSocket controlSocket : controlSockets) {
			try {
				controlSocket.setServerListen(serverListenTemp);
			} catch (Throwable e) {
				// do nothing
			}
		}
	}
}
