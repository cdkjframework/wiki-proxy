package com.framewiki.proxy.core.api.passway;

import com.cdkjframework.util.log.LogUtils;
import com.framewiki.proxy.core.api.IBelongControl;
import com.framewiki.proxy.core.api.secret.ISecret;
import com.framewiki.proxy.core.channel.impl.LengthChannelBase;
import com.framewiki.proxy.core.executor.FrameExecutor;
import com.framewiki.proxy.core.nio.impl.NioHallows;
import com.framewiki.proxy.core.util.ToolsUtils;
import lombok.Data;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Objects;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.api.passway
 * @ClassName: SecretPassway
 * @Description: 加密型隧道，一侧加密，一侧原样输入、输出
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
@Data

public class SecretPassway implements Runnable {
	/**
	 * 日志
	 */
	private final LogUtils log = LogUtils.getLogger(SecretPassway.class);

	/**
	 * 密钥
	 */
	private ISecret secret;

	/**
	 * 是否有效
	 */
	private boolean alive = false;
	/**
	 * 通道模式
	 */
	private Mode mode;
	/**
	 * 通道模式
	 */
	private IBelongControl belongControl;
	/**
	 * 缓存大小
	 */
	private int streamCacheSize = 8192;
	/**
	 * 接收通道
	 */
	private Socket recvSocket;
	/**
	 * 发送通道
	 */
	private Socket sendSocket;

	/**
	 * 从加密侧输入，解密后输出到无加密侧
	 *
	 * @throws Exception
	 */
	private void secretToNo() throws Exception {
		LengthChannelBase recvChannel = new LengthChannelBase(this.recvSocket);

		OutputStream outputStream = this.sendSocket.getOutputStream();
		SocketChannel outputChannel = this.sendSocket.getChannel();

		ISecret secret = this.secret;

		byte[] read;
		byte[] decrypt;
		while (this.alive) {
			read = recvChannel.read();
			if (read == null) {
				break;
			}
			decrypt = secret.decrypt(read);

			if (Objects.isNull(outputChannel)) {
				outputStream.write(decrypt);
				outputStream.flush();
			} else {
				ToolsUtils.channelWrite(outputChannel, ByteBuffer.wrap(decrypt));
			}
		}
	}

	/**
	 * 运行
	 */
	@Override
	public void run() {
		try {
			if (Mode.noToSecret.equals(this.mode)) {
				noToSecret();
			} else {
				secretToNo();
			}
		} catch (Exception e) {
			//
		}
		log.debug("one InputToOutputThread closed");
		// 传输完成后退出
		this.cancel();
	}

	/**
	 * 从无加密侧，经过加密后输出到加密侧
	 *
	 * @throws Exception
	 */
	private void noToSecret() throws Exception {
		InputStream inputStream = this.recvSocket.getInputStream();

		LengthChannelBase sendChannel = new LengthChannelBase(this.sendSocket);

		// 字段赋值局部变量，入栈
		boolean alive = this.alive;
		ISecret secret = this.secret;

		int len;
		byte[] arrayTemp = new byte[this.streamCacheSize];
		byte[] encrypt;
		while (alive && (len = inputStream.read(arrayTemp)) > 0) {
			encrypt = secret.encrypt(arrayTemp, 0, len);
			sendChannel.writeAndFlush(encrypt);
		}
	}

	/**
	 * 退出
	 */
	public void cancel() {
		if (!this.alive) {
			return;
		}
		this.alive = false;
		NioHallows.release(this.recvSocket.getChannel());
		try {
			Socket sendSocket;
			if ((sendSocket = this.sendSocket) != null) {
				// TCP 挥手步骤，对方调用 shutdownOutput 后等价完成 socket.close
				sendSocket.shutdownOutput();
			}
		} catch (IOException e) {
			// do nothing
		}

		IBelongControl belong;
		if ((belong = this.belongControl) != null) {
			this.belongControl = null;
			belong.noticeStop();
		}
	}

	/**
	 * 判断是否有效
	 *
	 * @return
	 */
	public boolean isValid() {
		return this.alive;
	}

	/**
	 * 启动
	 */
	public void start() {
		if (this.alive) {
			return;
		}
		this.alive = true;
		FrameExecutor.executePassway(this);
	}

	/**
	 * 通道模式
	 */
	public enum Mode {
		/**
		 * 从无加密接受到加密输出
		 */
		noToSecret,
		/**
		 * 从加密接受到无加密输出
		 */
		secretToNo
	}

}
