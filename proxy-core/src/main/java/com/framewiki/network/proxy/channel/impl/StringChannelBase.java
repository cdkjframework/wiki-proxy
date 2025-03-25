package com.framewiki.network.proxy.channel.impl;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.channel
 * @ClassName: StringChannelBase
 * @Description: 字符型通道
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public class StringChannelBase extends BaseSocketChannel<String, String> {

	/**
	 * 长度通道
	 */
	private final LengthChannelBase channel;

	/**
	 * 字符编码
	 */
	private Charset charset = StandardCharsets.UTF_8;

	/**
	 * 默认构造方法
	 */
	public StringChannelBase() {
		this.channel = new LengthChannelBase();
	}

	/**
	 * 构造方法
	 *
	 * @param socket 通信socket
	 * @throws IOException 创建异常
	 */
	public StringChannelBase(Socket socket) throws IOException {
		this.channel = new LengthChannelBase(socket);
	}

	/**
	 * 读取数据
	 *
	 * @return 读取的数据
	 * @throws Exception 读取异常
	 */
	@Override
	public String read() throws Exception {
		byte[] read = this.channel.read();
		return new String(read, this.charset);
	}

	/**
	 * 统一数据转换方法
	 *
	 * @param value 待写入的数据
	 * @return 转换后的数据
	 */
	private byte[] valueConvert(String value) {
		return value.getBytes(this.charset);
	}

	/**
	 * 写入数据
	 *
	 * @param value 待写入的数据
	 * @throws Exception 写入数据异常
	 */
	@Override
	public void write(String value) throws Exception {
		this.channel.write(this.valueConvert(value));
	}

	/**
	 * 刷新数据
	 *
	 * @throws Exception 刷新异常
	 */
	@Override
	public void flush() throws Exception {
		this.channel.flush();
	}

	/**
	 * 写入并刷新数据
	 *
	 * @param value 待写入的数据
	 * @throws Exception 写入并刷新异常
	 */
	@Override
	public void writeAndFlush(String value) throws Exception {
		this.channel.writeAndFlush(this.valueConvert(value));
	}

	/**
	 * 获取charset
	 *
	 * @return charset
	 */
	public Charset getCharset() {
		return this.charset;
	}

	/**
	 * 设置charset
	 *
	 * @param charset 编码
	 */
	@Override
	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	/**
	 * 获取socket
	 *
	 * @return socket
	 */
	@Override
	public Socket getSocket() {
		return this.channel.getSocket();
	}

	/**
	 * 设置socket
	 *
	 * @param socket socket
	 * @throws IOException 抛出IO异常
	 */
	@Override
	public void setSocket(Socket socket) throws IOException {
		this.channel.setSocket(socket);
	}

	/**
	 * 关闭socket
	 *
	 * @throws IOException 抛出IO异常
	 */
	@Override
	public void closeSocket() throws IOException {
		this.channel.closeSocket();
	}
}
