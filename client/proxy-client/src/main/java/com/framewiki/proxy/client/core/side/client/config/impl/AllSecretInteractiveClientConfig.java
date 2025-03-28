package com.framewiki.proxy.client.core.side.client.config.impl;

import com.framewiki.proxy.core.api.secret.impl.AesSecret;
import com.framewiki.proxy.core.api.socket.part.BaseSocketPart;
import com.framewiki.proxy.core.api.socket.part.SecretSocketPart;
import com.framewiki.proxy.client.core.side.client.ClientControlThread;
import com.framewiki.proxy.core.util.AesUtils;
import lombok.Getter;
import lombok.Setter;

import java.security.Key;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.client.core.side.client.config
 * @ClassName: AllSecretInteractiveClientConfig
 * @Description: 交互及隧道都加密
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public class AllSecretInteractiveClientConfig extends SecretInteractiveClientConfig {

	/**
	 * 隧道密钥
	 */
	@Setter
	@Getter
	private Key passwayKey;

	/**
	 * 创建隧道
	 *
	 * @param clientControlThread 客户端控制
	 * @return 隧道
	 */
	@Override
	public BaseSocketPart newSocketPart(ClientControlThread clientControlThread) {
		AesSecret secret = new AesSecret();
		secret.setAesKey(this.passwayKey);
		SecretSocketPart secretSocketPart = new SecretSocketPart(clientControlThread);
		secretSocketPart.setSecret(secret);
		return secretSocketPart;
	}

	/**
	 * base64格式设置密钥
	 *
	 * @param key base64格式密钥
	 */
	public void setBasePasswayKey(String key) {
		this.passwayKey = AesUtils.createKeyByBase64(key);
	}

}
