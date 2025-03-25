package com.framewiki.network.proxy.channel.impl;

import com.alibaba.fastjson.JSONObject;
import com.framewiki.network.proxy.model.InteractiveModel;
import com.framewiki.network.proxy.model.SecretInteractiveModel;
import com.framewiki.network.proxy.util.AesUtils;
import lombok.*;
import lombok.EqualsAndHashCode.Exclude;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.Key;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.channel
 * @ClassName: SecretInteractiveChannel
 * @Description: InteractiveModel 加密型通道，AES加密
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SecretInteractiveChannelBase extends BaseSocketChannel<InteractiveModel, InteractiveModel> {

	/**
	 * json通道
	 */
	@Exclude
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	private JsonChannelBase channel;

	/**
	 * 签名混淆key
	 */
	private String tokenKey;
	/**
	 * aes密钥
	 */
	private Key aesKey;
	/**
	 * 超时时间，毫秒
	 */
	private Long overtimeMills = 5000L;

	public SecretInteractiveChannelBase() {
		this.channel = new JsonChannelBase();
	}

	public SecretInteractiveChannelBase(Socket socket) throws IOException {
		this.channel = new JsonChannelBase(socket);
	}

	/**
	 * 读取数据
	 *
	 * @return InteractiveModel
	 * @throws Exception 读取数据异常
	 */
	@Override
	public InteractiveModel read() throws Exception {
		JSONObject read = this.channel.read();
		SecretInteractiveModel secretInteractiveModel = read.toJavaObject(SecretInteractiveModel.class);
		if (Math.abs(System.currentTimeMillis() - secretInteractiveModel.getTimestamp()) > this.overtimeMills) {
			throw new IllegalStateException("超时");
		}
		boolean checkAutograph = secretInteractiveModel.checkAutograph(this.tokenKey);
		if (!checkAutograph) {
			throw new IllegalStateException("签名错误");
		}
		secretInteractiveModel.decryptMsg(this.aesKey);
		return secretInteractiveModel;
	}

	/**
	 * 统一数据转换方法，使 {@link #write(InteractiveModel)} 与
	 * {@link #writeAndFlush(InteractiveModel)} 转换结果保持一致
	 *
	 * @param value 待转换数据
	 * @return 转换后的数据
	 * @throws Exception 转换异常
	 */
	private Object valueConvert(InteractiveModel value) throws Exception {
		SecretInteractiveModel secretInteractiveModel = new SecretInteractiveModel(value);
		secretInteractiveModel.setCharset(this.getCharset().name());
		secretInteractiveModel.fullMessage(this.aesKey, this.tokenKey);
		return secretInteractiveModel;
	}

	/**
	 * 写数据
	 *
	 * @param value 待写入数据
	 * @throws Exception 写入数据异常
	 */
	@Override
	public void write(InteractiveModel value) throws Exception {
		this.channel.write(this.valueConvert(value));
	}

	/**
	 * 刷新数据
	 *
	 * @throws Exception 刷新数据异常
	 */
	@Override
	public void flush() throws Exception {
		this.channel.flush();
	}

	/**
	 * 写数据并刷新
	 *
	 * @param value
	 * @throws Exception
	 */
	@Override
	public void writeAndFlush(InteractiveModel value) throws Exception {
		this.channel.writeAndFlush(this.valueConvert(value));
	}

	/**
	 * 获取charset
	 *
	 * @return
	 */
	public Charset getCharset() {
		return this.channel.getCharset();
	}

	/**
	 * 设置charset
	 *
	 * @param charset
	 */
	@Override
	public void setCharset(Charset charset) {
		this.channel.setCharset(charset);
	}

	/**
	 * 获取socket
	 *
	 * @return
	 */
	@Override
	public Socket getSocket() {
		return this.channel.getSocket();
	}

	/**
	 * 设置socket
	 *
	 * @param socket socket
	 * @throws IOException
	 */
	@Override
	public void setSocket(Socket socket) throws IOException {
		this.channel.setSocket(socket);
	}

	/**
	 * 关闭socket
	 *
	 * @throws IOException
	 */
	@Override
	public void closeSocket() throws IOException {
		this.channel.closeSocket();
	}

	/**
	 * 使用base64格式设置aes密钥
	 *
	 * @param aesKey
	 */
	public void setBaseAesKey(String aesKey) {
		this.aesKey = AesUtils.createKeyByBase64(aesKey);
	}

}
