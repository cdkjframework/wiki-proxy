package com.framewiki.proxy.core.channel.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONAware;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.channel
 * @ClassName: JsonChannelBase
 * @Description: json方式读写
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public class JsonChannelBase extends BaseSocketChannel<JSONObject, Object> {

	/**
	 * 实际通道
	 */
	private final StringChannelBase channel;

	/**
	 * 构造函数
	 */
	public JsonChannelBase() {
		this.channel = new StringChannelBase();
	}

	/**
	 * 构造函数
	 *
	 * @param socket 通道
	 * @throws IOException 创建异常
	 */
	public JsonChannelBase(Socket socket) throws IOException {
		this.channel = new StringChannelBase(socket);
	}

	/**
	 * 读取数据
	 *
	 * @return 读取的数据
	 * @throws Exception 读取异常
	 */
	@Override
	public JSONObject read() throws Exception {
		String read = this.channel.read();
		return JSON.parseObject(read);
	}

	/**
	 * 写入数据
	 *
	 * @param value 值
	 * @return 写入结果
	 */
	private String valueConvert(Object value) {
		String string;
		if (value instanceof JSONAware) {
			string = ((JSONAware) value).toJSONString();
		} else {
			string = JSON.toJSONString(value);
		}
		return string;
	}

	/**
	 * 写入数据
	 *
	 * @param value 待写入的数据
	 * @throws Exception 写入异常
	 */
	@Override
	public void write(Object value) throws Exception {
		this.channel.write(this.valueConvert(value));
	}

	/**
	 * 刷新
	 *
	 * @throws Exception 刷新异常
	 */
	@Override
	public void flush() throws Exception {
		this.channel.flush();
	}

	/**
	 * 写入并刷新
	 *
	 * @param value 待写入的数据
	 * @throws Exception 写入并刷新异常
	 */
	@Override
	public void writeAndFlush(Object value) throws Exception {
		this.channel.writeAndFlush(this.valueConvert(value));
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
	 * 获取通道
	 *
	 * @return 通道
	 */
	@Override
	public Socket getSocket() {
		return this.channel.getSocket();
	}

	/**
	 * 设置通道
	 *
	 * @param socket socket
	 * @throws IOException 创建异常
	 */
	@Override
	public void setSocket(Socket socket) throws IOException {
		this.channel.setSocket(socket);
	}

	/**
	 * 关闭通道
	 *
	 * @throws IOException 关闭异常
	 */
	@Override
	public void closeSocket() throws IOException {
		this.channel.closeSocket();
	}
}
