package com.framewiki.network.proxy.channel.impl;

import com.framewiki.network.proxy.util.ToolsUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.channel
 * @ClassName: LengthChannel
 * @Description: 长度限定读写通道
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public class LengthChannelBase extends BaseSocketChannel<byte[], byte[]> {

	private final byte[] lenBytes = new byte[4];
	/**
	 * 读写锁
	 */
	private final ReentrantLock readLock = new ReentrantLock(Boolean.TRUE);
	/**
	 * 写锁
	 */
	private final ReentrantLock writerLock = new ReentrantLock(Boolean.TRUE);
	/**
	 * socket
	 */
	private Socket socket;
	/**
	 * socket输入流
	 */
	private InputStream inputStream;
	/**
	 * socket输出流
	 */
	private OutputStream outputStream;
	/**
	 * socket通道
	 */
	private java.nio.channels.SocketChannel socketChannel;

	/**
	 * 构造方法
	 */
	public LengthChannelBase() {
	}

	/**
	 * 构造方法
	 *
	 * @param socket socket
	 * @throws IOException 抛出IO异常
	 */
	public LengthChannelBase(Socket socket) throws IOException {
		this.setSocket(socket);
	}

	/**
	 * 读取数据
	 *
	 * @return byte[]
	 * @throws Exception 读取数据异常
	 */
	@Override
	public byte[] read() throws Exception {
		ReentrantLock readLock = this.readLock;
		byte[] lenBytes = this.lenBytes;
		readLock.lock();
		try {
			int offset = 0;
			InputStream is = getInputSteam();
			int len;
			while (offset < lenBytes.length) {
				len = is.read(lenBytes, offset, lenBytes.length - offset);
				if (len < 0) {
					// 如果-1，提前关闭了，又没有获得足够的数据，那么就抛出异常
					throw new IOException("Insufficient byte length[" + lenBytes.length + "] when io closed");
				}
				offset += len;
			}
			int length = ToolsUtils.bytesToInt(lenBytes);

			offset = 0;
			byte[] b = new byte[length];
			while (offset < length) {
				len = is.read(b, offset, length - offset);
				if (len < 0) {
					// 如果-1，提前关闭了，又没有获得足够的数据，那么就抛出异常
					throw new IOException("Insufficient byte length[" + length + "] when io closed");
				}
				offset += len;
			}
			return b;
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 写入数据
	 *
	 * @param value 值
	 * @throws Exception 写入数据异常
	 */
	@Override
	public void write(byte[] value) throws Exception {
		ReentrantLock writerLock = this.writerLock;

		writerLock.lock();
		try {
			java.nio.channels.SocketChannel socketChannel;
			if (Objects.nonNull(socketChannel = this.socketChannel)) {
				ToolsUtils.channelWrite(socketChannel, ByteBuffer.wrap(ToolsUtils.intToBytes(value.length)));
				ToolsUtils.channelWrite(socketChannel, ByteBuffer.wrap(value));
			} else {
				OutputStream os = getOutputStream();
				os.write(ToolsUtils.intToBytes(value.length));
				os.write(value);
			}
		} finally {
			writerLock.unlock();
		}
	}

	/**
	 * 刷新
	 *
	 * @throws Exception 刷新异常
	 */
	@Override
	public void flush() throws Exception {
		ReentrantLock writerLock = this.writerLock;

		writerLock.lock();
		try {
			getOutputStream().flush();
		} finally {
			writerLock.unlock();
		}
	}

	/**
	 * 写入并刷新
	 *
	 * @param value 值
	 * @throws Exception 写入并刷新异常
	 */
	@Override
	public void writeAndFlush(byte[] value) throws Exception {
		ReentrantLock writerLock = this.writerLock;
		writerLock.lock();
		try {
			this.write(value);
			this.flush();
		} finally {
			writerLock.unlock();
		}
	}

	/**
	 * 获取socket
	 *
	 * @return Socket
	 */
	@Override
	public Socket getSocket() {
		return this.socket;
	}

	/**
	 * 设置socket
	 *
	 * @param socket socket
	 * @throws IOException 抛出IO异常
	 */
	@Override
	public void setSocket(Socket socket) throws IOException {
		if (Objects.nonNull(this.socket)) {
			throw new UnsupportedOperationException("socket cannot be set repeatedly");
		}
		this.socket = socket;
		this.socketChannel = socket.getChannel();
		this.inputStream = socket.getInputStream();
		this.outputStream = socket.getOutputStream();
	}

	/**
	 * 关闭socket
	 *
	 * @throws IOException 抛出IO异常
	 */
	@Override
	public void closeSocket() throws IOException {
		this.socket.close();
	}

	/**
	 * 惰性获取输入流
	 *
	 * @return 返回流
	 * @throws IOException 抛出IO异常
	 */
	private InputStream getInputSteam() throws IOException {
		InputStream inputStream;
		if ((inputStream = this.inputStream) == null) {
			inputStream = this.inputStream = this.socket.getInputStream();
		}
		return inputStream;
	}

	/**
	 * 惰性获取输出流
	 *
	 * @return 返回流
	 * @throws IOException 抛出IO异常
	 */
	private OutputStream getOutputStream() throws IOException {
		OutputStream outputStream;
		if ((outputStream = this.outputStream) == null) {
			outputStream = this.outputStream = this.getSocket().getOutputStream();
		}
		return outputStream;
	}

}
