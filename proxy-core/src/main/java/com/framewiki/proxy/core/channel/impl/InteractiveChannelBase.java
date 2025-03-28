package com.framewiki.proxy.core.channel.impl;

import com.alibaba.fastjson.JSONObject;
import com.framewiki.proxy.core.model.InteractiveModel;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * InteractiveModel 模式读写
 *
 */
/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.channel
 * @ClassName: InteractiveChannelBase
 * @Description: InteractiveModel 模式读写
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public class InteractiveChannelBase extends BaseSocketChannel<InteractiveModel, InteractiveModel> {

	/**
	 * 实际通道
	 */
	private final JsonChannelBase channel;

	/**
	 * 构造函数
	 */
	public InteractiveChannelBase() {
		this.channel = new JsonChannelBase();
	}

	/**
	 * 构造函数
	 *
	 * @param socket socket
	 * @throws IOException 抛出IO异常
	 */
	public InteractiveChannelBase(Socket socket) throws IOException {
		this.channel = new JsonChannelBase(socket);
	}

	/**
	 * 读取数据
	 *
	 * @return 读取的数据
	 * @throws Exception 读取异常
	 */
	@Override
	public InteractiveModel read() throws Exception {
		JSONObject read = this.channel.read();
		return read.toJavaObject(InteractiveModel.class);
	}

	/**
	 * 写入数据
	 *
	 * @param value 待写入的数据
	 * @throws Exception 写入异常
	 */
	@Override
	public void write(InteractiveModel value) throws Exception {
		this.channel.write(value);
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
	public void writeAndFlush(InteractiveModel value) throws Exception {
		this.channel.writeAndFlush(value);
	}

	/**
	 * 获取 charset
	 *
	 * @return charset
	 */
	public Charset getCharset() {
		return this.channel.getCharset();
	}

	/**
	 * 设置 charset
	 *
	 * @param charset 编码
	 */
	@Override
	public void setCharset(Charset charset) {
		this.channel.setCharset(charset);
	}

	/**
	 * 获取 socket
	 *
	 * @return socket
	 */
	@Override
	public Socket getSocket() {
		return this.channel.getSocket();
	}

	/**
	 * 设置 socket
	 *
	 * @param socket socket
	 * @throws IOException 抛出IO异常
	 */
	@Override
	public void setSocket(Socket socket) throws IOException {
		this.channel.setSocket(socket);
	}

	/**
	 * 关闭
	 *
	 * @throws IOException 抛出IO异常
	 */
	@Override
	public void closeSocket() throws IOException {
		this.channel.closeSocket();
	}
}
