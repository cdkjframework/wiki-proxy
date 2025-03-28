package com.framewiki.proxy.core.api.secret;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.api.secret
 * @ClassName: ISecret
 * @Description: 加密方法
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public interface ISecret {

	/**
	 * 加密数据
	 *
	 * @param content 加密内容字节
	 * @param offset  加密内容字节偏移量
	 * @param len     长度
	 * @return 加密后字节
	 * @throws Exception 错误
	 */
	byte[] encrypt(byte[] content, int offset, int len) throws Exception;

	/**
	 * 解密数据
	 *
	 * @param result 解密内容
	 * @return 解密后字节
	 * @throws Exception 错误
	 */
	byte[] decrypt(byte[] result) throws Exception;
}
