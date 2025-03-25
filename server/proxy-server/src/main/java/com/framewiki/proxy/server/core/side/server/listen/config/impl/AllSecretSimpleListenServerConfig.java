package com.framewiki.proxy.server.core.side.server.listen.config.impl;

import com.framewiki.proxy.server.core.side.server.listen.ServerListenThread;
import com.framewiki.network.proxy.api.secret.impl.AesSecret;
import com.framewiki.network.proxy.api.socket.part.BaseSocketPart;
import com.framewiki.network.proxy.api.socket.part.SecretSocketPart;
import com.framewiki.network.proxy.util.AesUtils;
import lombok.Getter;
import lombok.Setter;

import java.security.Key;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.side.server.listen.config
 * @ClassName: AllSecretSimpleListenServerConfig
 * @Description: 交互及隧道都加密
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public class AllSecretSimpleListenServerConfig extends SecretSimpleListenServerConfig {

	/**
	 * 隧道加密密钥
	 */
	@Setter
	@Getter
	private Key passwayKey;

	/**
	 * 构造
	 *
	 * @param listenPort 监听端口
	 */
	public AllSecretSimpleListenServerConfig(Integer listenPort) {
		super(listenPort);
	}

	/**
	 * 创建SocketPart
	 *
	 * @param serverListenThread 服务器监听线程
	 * @return
	 */
	@Override
	public BaseSocketPart newSocketPart(ServerListenThread serverListenThread) {
		AesSecret secret = new AesSecret();
		secret.setAesKey(this.passwayKey);
		SecretSocketPart secretSocketPart = new SecretSocketPart(serverListenThread);
		secretSocketPart.setSecret(secret);
		return secretSocketPart;
	}

	/**
	 * BASE64格式设置隧道加密密钥
	 *
	 * @param key 密钥
	 */
	public void setBasePasswayKey(String key) {
		this.passwayKey = AesUtils.createKeyByBase64(key);
	}

}
