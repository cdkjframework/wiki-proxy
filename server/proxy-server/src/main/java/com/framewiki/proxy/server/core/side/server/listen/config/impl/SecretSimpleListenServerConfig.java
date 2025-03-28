package com.framewiki.proxy.server.core.side.server.listen.config.impl;

import com.cdkjframework.util.log.LogUtils;
import com.framewiki.proxy.core.channel.impl.SecretInteractiveChannelBase;
import com.framewiki.proxy.core.channel.impl.BaseSocketChannel;
import com.framewiki.proxy.core.model.InteractiveModel;
import com.framewiki.proxy.core.util.AesUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


import java.io.IOException;
import java.net.Socket;
import java.security.Key;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.side.server.listen.config
 * @ClassName: SecretSimpleListenServerConfig
 * @Description: 交互加密配置
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SecretSimpleListenServerConfig extends SimpleListenServerConfig {
	/**
	 * 日志
	 */
	private final LogUtils log = LogUtils.getLogger(SecretSimpleListenServerConfig.class);

	/**
	 * 交互加密密钥
	 */
	private String tokenKey;

	/**
	 * AES加密密钥
	 */
	private Key aesKey;

	/**
	 * 构造方法
	 *
	 * @param listenPort 监听端口
	 */
	public SecretSimpleListenServerConfig(Integer listenPort) {
		super(listenPort);
	}

	/**
	 * 创建控制连接通道
	 *
	 * @param socket 控制连接
	 * @return 通信通道
	 */
	@Override
	protected BaseSocketChannel<? extends InteractiveModel, ? super InteractiveModel> newControlSocketChannel(
			Socket socket) {
		SecretInteractiveChannelBase channel = new SecretInteractiveChannelBase();
		channel.setCharset(this.getCharset());
		channel.setTokenKey(this.tokenKey);
		channel.setAesKey(this.aesKey);
		try {
			channel.setSocket(socket);
		} catch (IOException e) {
			log.error("newControlSocketChannel exception", e);
			return null;
		}
		return channel;
	}

	/**
	 * BASE64格式设置交互加密密钥
	 *
	 * @param aesKey BASE64格式的AES密钥
	 */
	public void setBaseAesKey(String aesKey) {
		this.aesKey = AesUtils.createKeyByBase64(aesKey);
	}

}
