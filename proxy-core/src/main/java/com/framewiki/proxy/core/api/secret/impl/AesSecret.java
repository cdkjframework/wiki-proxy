package com.framewiki.proxy.core.api.secret.impl;

import com.framewiki.proxy.core.api.secret.ISecret;
import com.framewiki.proxy.core.util.AesUtils;
import lombok.Data;

import java.security.Key;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.api
 * @ClassName: AesSecret
 * @Description: AES加密方式
 * @Author: frank tiger
 * @Date: 2025/1/2 13:59
 * @Version: 1.0
 */
@Data
public class AesSecret implements ISecret {

	/**
	 * 密钥
	 */
	private Key aesKey;

	/**
	 * 加密
	 *
	 * @param content 内容
	 * @param offset  抵消
	 * @param len     长度
	 * @return byte[]
	 * @throws Exception 错误
	 */
	@Override
	public byte[] encrypt(byte[] content, int offset, int len) throws Exception {
		return AesUtils.encrypt(this.aesKey, content, offset, len);
	}


	/**
	 * 解密
	 *
	 * @param result 解密内容
	 * @return byte[]
	 * @throws Exception 错误
	 */
	@Override
	public byte[] decrypt(byte[] result) throws Exception {
		return AesUtils.decrypt(this.aesKey, result);
	}

	/**
	 * 设置密钥
	 *
	 * @param aesKey 密钥
	 */
	public void setBaseAesKey(String aesKey) {
		this.aesKey = AesUtils.createKeyByBase64(aesKey);
	}

}
