package com.framewiki.proxy.core.api.socket.part;

import com.cdkjframework.util.log.LogUtils;
import com.framewiki.proxy.core.api.IBelongControl;
import com.framewiki.proxy.core.api.passway.SecretPassway;
import com.framewiki.proxy.core.api.secret.ISecret;
import com.framewiki.proxy.core.model.FrameResultModel;
import com.framewiki.proxy.core.util.AssertUtils;
import lombok.Getter;
import lombok.Setter;


import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.api.socketpart
 * @ClassName: SecretSocketPart
 * @Description: 加密-无加密socket对
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */

public class SecretSocketPart extends BaseSocketPart implements IBelongControl {
	/**
	 * 日志
	 */
	private final LogUtils log = LogUtils.getLogger(FrameResultModel.class);

	/**
	 * 停止锁
	 */
	private final CountDownLatch cancelLatch = new CountDownLatch(2);
	/**
	 * 密钥
	 */
	@Getter
	@Setter
	private ISecret secret;
	/**
	 * 监听端口-加密
	 */
	private SecretPassway noToSecretPassway;
	/**
	 * 监听端口-解密
	 */
	private SecretPassway secretToNoPassway;
	/**
	 * 缓存大小
	 */
	@Getter
	@Setter
	private int streamCacheSize = 8192;

	/**
	 * 构造
	 *
	 * @param belongThread 所属线程
	 */
	public SecretSocketPart(IBelongControl belongThread) {
		super(belongThread);
	}

	/**
	 * 取消
	 */
	@Override
	public void cancel() {
		if (this.canceled) {
			return;
		}
		this.canceled = true;
		this.isAlive = false;

		log.debug("socketPart {} will cancel", this.socketPartKey);

		SecretPassway noToSecretPassway;
		if ((noToSecretPassway = this.noToSecretPassway) != null) {
			this.noToSecretPassway = null;
			noToSecretPassway.cancel();
		}
		SecretPassway secretToNoPassway;
		if ((secretToNoPassway = this.secretToNoPassway) != null) {
			this.secretToNoPassway = null;
			secretToNoPassway.cancel();
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
	 * @return boolean
	 */
	@Override
	public boolean createPassWay() {
		AssertUtils.state(!this.canceled, "不得重启已退出的socketPart");

		if (this.isAlive) {
			return true;
		}
		this.isAlive = true;
		try {
			// 主要面向服务端-客户端过程加密
			SecretPassway noToSecretPassway = this.noToSecretPassway = new SecretPassway();
			noToSecretPassway.setBelongControl(this);
			noToSecretPassway.setMode(SecretPassway.Mode.noToSecret);
			noToSecretPassway.setRecvSocket(this.recvSocket);
			noToSecretPassway.setSendSocket(this.sendSocket);
			noToSecretPassway.setStreamCacheSize(getStreamCacheSize());
			noToSecretPassway.setSecret(this.secret);

			SecretPassway secretToNoPassway = this.secretToNoPassway = new SecretPassway();
			secretToNoPassway.setBelongControl(this);
			secretToNoPassway.setMode(SecretPassway.Mode.secretToNo);
			secretToNoPassway.setRecvSocket(this.sendSocket);
			secretToNoPassway.setSendSocket(this.recvSocket);
			secretToNoPassway.setSecret(this.secret);

			noToSecretPassway.start();
			secretToNoPassway.start();
		} catch (Exception e) {
			log.error("socketPart [" + this.socketPartKey + "] 隧道建立异常", e);
			this.stop();
			return false;
		}
		return true;
	}

	/**
	 * 停止
	 */
	public void stop() {
		this.cancel();

		IBelongControl belong;
		if ((belong = this.belongThread) != null) {
			this.belongThread = null;
			belong.noticeStop();
		}
	}

	/**
	 * 通知停止
	 */
	@Override
	public void noticeStop() {
		CountDownLatch cancelLatch = this.cancelLatch;
		cancelLatch.countDown();
		if (cancelLatch.getCount() <= 0) {
			this.stop();
		}
	}

}
