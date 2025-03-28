package com.framewiki.proxy.core.api.socket.part;

import com.framewiki.proxy.core.api.IBelongControl;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.api.socketpart
 * @ClassName: BaseSocketPart
 * @Description: socketPart抽象类
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
@Data
public abstract class BaseSocketPart {

	/**
	 * 是否有效
	 */
	@Setter(AccessLevel.PRIVATE)
	protected volatile boolean isAlive = false;

	/**
	 * 是否取消
	 */
	@Setter(AccessLevel.PRIVATE)
	protected volatile boolean canceled = false;

	/**
	 * 创建时间
	 */
	@Setter(AccessLevel.PRIVATE)
	protected LocalDateTime createTime;

	/**
	 * 隧道标识
	 */
	protected String socketPartKey;

	/**
	 * 所属线程
	 */
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.PROTECTED)
	protected IBelongControl belongThread;

	/**
	 * 等待连接有效时间，ms
	 */
	@Getter
	@Setter
	private long invalidMillis = 60000L;
	/**
	 * 接受数据的socket；接受与发送的区分主要是主动发送方
	 */
	protected Socket recvSocket;
	/**
	 * 发送的socket
	 */
	protected Socket sendSocket;

	/**
	 * 构造
	 *
	 * @param belongThread 所属线程
	 */
	protected BaseSocketPart(IBelongControl belongThread) {
		this.belongThread = belongThread;
		this.createTime = LocalDateTime.now();
	}

	/**
	 * 是否有效
	 */
	public boolean isValid() {
		if (this.canceled) {
			return false;
		}

		if (this.isAlive) {
			return true;
		}

		long millis = Duration.between(this.createTime, LocalDateTime.now()).toMillis();
		return millis < this.invalidMillis;
	}

	/**
	 * 退出
	 */
	public abstract void cancel();

	/**
	 * 打通隧道
	 *
	 * @return
	 */
	public abstract boolean createPassWay();

}
