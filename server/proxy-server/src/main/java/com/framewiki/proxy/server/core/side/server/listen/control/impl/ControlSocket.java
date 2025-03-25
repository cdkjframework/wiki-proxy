package com.framewiki.proxy.server.core.side.server.listen.control.impl;

import com.cdkjframework.util.log.LogUtils;
import com.framewiki.network.proxy.channel.impl.BaseSocketChannel;
import com.framewiki.network.proxy.model.InteractiveModel;
import com.framewiki.network.proxy.model.enums.FrameResultEnum;
import com.framewiki.network.proxy.model.enums.InteractiveTypeEnum;
import com.framewiki.network.proxy.model.interactive.ServerWaitModel;
import com.framewiki.proxy.server.core.side.server.client.handler.impl.InteractiveProcessHandler;
import com.framewiki.proxy.server.core.side.server.listen.ServerListen;
import com.framewiki.proxy.server.core.side.server.listen.control.IControlSocket;
import com.framewiki.proxy.server.core.side.server.listen.recv.IRecvHandler;


import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.side.server.listen.config
 * @ClassName: ControlSocket
 * @Description: 控制socket实例
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */

public class ControlSocket implements IControlSocket, Runnable {
	/**
	 * 日志
	 */
	private final LogUtils log = LogUtils.getLogger(ControlSocket.class);

	/**
	 * socket通道
	 */
	protected final BaseSocketChannel<? extends InteractiveModel, ? super InteractiveModel> baseSocketChannel;

	/**
	 * 接收处理器
	 */
	protected List<IRecvHandler<? super InteractiveModel, ? extends InteractiveModel>> recvHandlerList = new LinkedList<>();

	/**
	 * 服务监听
	 */
	protected ServerListen serverListen;
	/**
	 * 线程
	 */
	private volatile Thread myThread = null;

	/**
	 * 是否启动
	 */
	private volatile boolean started = false;

	/**
	 * 是否取消
	 */
	private volatile boolean cancelled = false;

	/**
	 * 构造
	 *
	 * @param baseSocketChannel 通道
	 */
	public ControlSocket(BaseSocketChannel<? extends InteractiveModel, ? super InteractiveModel> baseSocketChannel) {
		this.baseSocketChannel = baseSocketChannel;
	}

	/**
	 * 是否有效
	 *
	 * @return
	 */
	@Override
	public boolean isValid() {
		BaseSocketChannel<? extends InteractiveModel, ? super InteractiveModel> baseSocketChannel = this.baseSocketChannel;
		Socket socket = (baseSocketChannel == null) ? null : baseSocketChannel.getSocket();
		boolean closeFlag = socket == null || !socket.isConnected() || socket.isClosed() || socket.isInputShutdown() ||
				socket.isOutputShutdown();
		if (closeFlag) {
			return false;
		}

		try {
			// 心跳测试
			InteractiveModel interactiveModel = InteractiveModel.of(InteractiveTypeEnum.HEART_TEST, null);
			baseSocketChannel.writeAndFlush(interactiveModel);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 * 关闭
	 */
	@Override
	public void close() {
		if (this.cancelled) {
			return;
		}
		this.cancelled = true;

		Thread myThread;
		if ((myThread = this.myThread) != null) {
			this.myThread = null;
			myThread.interrupt();
		}

		if (this.baseSocketChannel != null) {
			try {
				this.baseSocketChannel.close();
			} catch (IOException e) {
				// do nothing
			}
		}

		ServerListen serverListenThread = this.serverListen;
		if (Objects.nonNull(serverListenThread)) {
			this.serverListen = null;
			serverListenThread.controlCloseNotice(this);
		}

	}

	/**
	 * 发送等待客户端连接
	 *
	 * @param socketPartKey 隧道标识
	 * @return
	 */
	@Override
	public boolean sendClientWait(String socketPartKey) {
		InteractiveModel model = InteractiveModel.of(InteractiveTypeEnum.SERVER_WAIT_CLIENT,
				new ServerWaitModel(socketPartKey));

		try {
			this.baseSocketChannel.writeAndFlush(model);
		} catch (Throwable e) {
			return false;
		}

		return true;
	}

	/**
	 * 启动接收
	 */
	@Override
	public void startRecv() {
		if (this.started) {
			return;
		}
		this.started = true;

		Thread myThread = this.myThread;
		if (myThread == null || !myThread.isAlive()) {
			myThread = this.myThread = new Thread(this);
			myThread.setName("control-recv-" + this.formatServerListenInfo());
			myThread.start();
		}
	}

	/**
	 * 运行
	 */
	@Override
	public void run() {
		BaseSocketChannel<? extends InteractiveModel, ? super InteractiveModel> baseSocketChannel = this.baseSocketChannel;
		while (this.started && !this.cancelled) {
			try {
				InteractiveModel interactiveModel = baseSocketChannel.read();

				log.info("监听线程 [{}] 接收到控制端口发来的消息：[ {} ]", this.formatServerListenInfo(),
						interactiveModel);

				boolean proc = false;
				for (IRecvHandler<? super InteractiveModel, ? extends InteractiveModel> handler : this.recvHandlerList) {
					proc = handler.proc(interactiveModel, this.baseSocketChannel);
					if (proc) {
						break;
					}
				}

				if (!proc) {
					log.warn(String.format("无处理方法的信息：[{}]", interactiveModel));

					InteractiveModel result = InteractiveModel.of(interactiveModel.getInteractiveSeq(),
							InteractiveTypeEnum.COMMON_REPLY,
							FrameResultEnum.UNKNOWN_INTERACTIVE_TYPE.toResultModel());
					baseSocketChannel.writeAndFlush(result);
				}

			} catch (Exception e) {
				log.error("读取或写入异常", e);
				if (e instanceof IOException || !this.isValid()) {
					this.close();
				}
			}
		}
	}

	/**
	 * 获取服务监听信息
	 *
	 * @return
	 */
	private String formatServerListenInfo() {
		if (Objects.isNull(this.serverListen)) {
			return null;
		}
		return this.serverListen.formatInfo();
	}

	/**
	 * 设置服务监听
	 *
	 * @param serverListen 监听线程
	 */
	@Override
	public void setServerListen(ServerListen serverListen) {
		this.serverListen = serverListen;
	}

	/**
	 * 添加处理器
	 *
	 * @param handler 处理器
	 * @return
	 */
	public ControlSocket addRecvHandler(IRecvHandler<? super InteractiveModel, ? extends InteractiveModel> handler) {
		this.recvHandlerList.add(handler);
		return this;
	}
}
