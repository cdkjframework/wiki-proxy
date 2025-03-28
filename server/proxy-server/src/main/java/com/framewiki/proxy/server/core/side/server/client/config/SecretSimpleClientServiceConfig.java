package com.framewiki.proxy.server.core.side.server.client.config;

import com.framewiki.proxy.core.channel.impl.SecretInteractiveChannelBase;
import com.framewiki.proxy.core.channel.impl.BaseSocketChannel;
import com.framewiki.proxy.core.model.InteractiveModel;
import com.framewiki.proxy.core.util.AesUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.net.Socket;
import java.security.Key;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.side.server.client.config
 * @ClassName: SecretSimpleClientServiceConfig
 * @Description: 隧道过程加密的配置类
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SecretSimpleClientServiceConfig extends SimpleClientServiceConfig {

	/**
	 * 签名混淆key
	 */
	private String tokenKey;
	/**
	 * 隧道过程加密key AES
	 */
	private Key aesKey;

	/**
	 * 创建配置
	 *
	 * @param listenPort 监听端口
	 */
	public SecretSimpleClientServiceConfig(Integer listenPort) {
		super(listenPort);
	}

	/**
	 * 创建socket通道
	 *
	 * @param listenSocket 监听socket
	 * @return socket通道
	 * @throws Exception 创建失败
	 */
	@Override
	public BaseSocketChannel<? extends InteractiveModel, ? super InteractiveModel> newSocketChannel(Socket listenSocket)
			throws Exception {
		SecretInteractiveChannelBase channel = new SecretInteractiveChannelBase();
		channel.setCharset(this.getCharset());
		channel.setTokenKey(this.tokenKey);
		channel.setAesKey(this.aesKey);
		channel.setSocket(listenSocket);
		return channel;
	}

	/**
	 * BASE64格式设置隧道加密密钥
	 *
	 * @param aesKey 隧道加密密钥
	 */
	public void setBaseAesKey(String aesKey) {
		this.aesKey = AesUtils.createKeyByBase64(aesKey);
	}
}
