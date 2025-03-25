package com.framewiki.network.proxy.api.socket.part;

import com.cdkjframework.util.log.LogUtils;
import com.framewiki.network.proxy.api.IBelongControl;
import com.framewiki.network.proxy.api.passway.SecretPassway;
import com.framewiki.network.proxy.api.passway.SimplePassway;
import com.framewiki.network.proxy.util.AssertUtils;
import lombok.Getter;
import lombok.Setter;


import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.api.socketpart
 * @ClassName: SimpleSocketPart
 * @Description: socket匹配对
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */

public class SimpleSocketPart extends BaseSocketPart implements IBelongControl {
	/**
	 * 日志
	 */
	private final LogUtils log = LogUtils.getLogger(SimpleSocketPart.class);

	/**
	 * 输出到输入的隧道
	 */
	protected SimplePassway outToInPassway;

	/**
	 * 输入到输出的隧道
	 */
	protected SimplePassway inToOutPassway;

	/**
	 * 停止标记
	 */
	private final CountDownLatch cancelLatch = new CountDownLatch(2);

	/**
	 * 缓存大小
	 */
	@Getter
	@Setter
	private int streamCacheSize = 8192;

	/**
	 * 构造
	 *
	 * @param belongThread 线程
	 */
	public SimpleSocketPart(IBelongControl belongThread) {
		super(belongThread);
	}

	/**
	 * 停止，并告知上层处理掉
	 */
	public void stop() {
		this.cancel();

		IBelongControl belong;
		if ((belong = this.belongThread) != null) {
			this.belongThread = null;
			belong.stopSocketPart(this.socketPartKey);
		}
	}

	/**
	 * 停止，并告知上层处理掉
	 */
	@Override
	public void cancel() {
		if (this.canceled) {
			return;
		}
		this.canceled = true;
		this.isAlive = false;

		log.debug("socketPart {} will cancel", this.socketPartKey);

		SimplePassway outToInPassway;
		if ((outToInPassway = this.outToInPassway) != null) {
			this.outToInPassway = null;
			outToInPassway.cancel();
		}
		SimplePassway inToOutPassway;
		if ((inToOutPassway = this.inToOutPassway) != null) {
			this.inToOutPassway = null;
			inToOutPassway.cancel();
		}

		Socket recvSocket;
		if ((recvSocket = this.recvSocket) != null) {
			this.recvSocket = null;
			try {
				recvSocket.close();
			} catch (IOException e) {
				log.debug("socketPart [{}] 监听端口 关闭异常", socketPartKey);
			}
		}

		Socket sendSocket;
		if ((sendSocket = this.sendSocket) != null) {
			this.sendSocket = null;
			try {
				sendSocket.close();
			} catch (IOException e) {
				log.debug("socketPart [{}] 发送端口 关闭异常", socketPartKey);
			}
		}

		log.debug("socketPart {} is cancelled", this.socketPartKey);
	}

	/**
	 * 创建隧道
	 * @return 是否成功
	 */
	@Override
	public boolean createPassWay() {
		AssertUtils.state(!this.canceled, "不得重启已退出的socketPart");

		if (this.isAlive) {
			return true;
		}
		this.isAlive = true;

		try {
			SimplePassway outToInPassway = this.outToInPassway = new SimplePassway();
			outToInPassway.setBelongControl(this);
			outToInPassway.setRecvSocket(this.recvSocket);
			outToInPassway.setSendSocket(this.sendSocket);
			outToInPassway.setStreamCacheSize(getStreamCacheSize());

			SimplePassway inToOutPassway = this.inToOutPassway = new SimplePassway();
			inToOutPassway.setBelongControl(this);
			inToOutPassway.setRecvSocket(this.sendSocket);
			inToOutPassway.setSendSocket(this.recvSocket);
			inToOutPassway.setStreamCacheSize(getStreamCacheSize());

			outToInPassway.start();
			inToOutPassway.start();
		} catch (Exception e) {
			log.error("socketPart [" + this.socketPartKey + "] 隧道建立异常", e);
			this.stop();
			return false;
		}
		return true;
	}

	/**
	 * 通知停止
	 */
	@Override
	public void noticeStop() {
		CountDownLatch cancellLatch = this.cancelLatch;
		cancellLatch.countDown();
		if (cancellLatch.getCount() <= 0) {
			this.stop();
		}
	}
}
