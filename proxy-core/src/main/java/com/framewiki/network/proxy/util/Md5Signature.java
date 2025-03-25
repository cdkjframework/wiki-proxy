package com.framewiki.network.proxy.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.util
 * @ClassName: Md5Signature
 * @Description: MD5散列签名
 * @Author: frank tiger
 * @Date: 2025/1/2 13:59
 * @Version: 1.0
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Md5Signature {

	/**
	 * 默认字符集
	 */
	private static final Charset CHARSET = StandardCharsets.UTF_8;

	/**
	 * 随机字符
	 */
	private static final String RANDOM_BASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	/**
	 * 随机数生成器
	 */
	private static final Random RANDOM = new Random();

	/**
	 * 16进制字符
	 */
	private static final String HEX_BASE = "0123456789abcdef";

	/**
	 * 转换为16进制字符
	 *
	 * @param bytes byte数组
	 * @return 16进制字符串
	 */
	public static String toHexString(byte[] bytes) {
		StringBuilder stringBuilder = new StringBuilder(bytes.length << 1);

		for (byte tmp : bytes) {
			stringBuilder.append(HEX_BASE.charAt(tmp >> 4 & 0xf));
			stringBuilder.append(HEX_BASE.charAt(tmp & 0xf));
		}
		return stringBuilder.toString();
	}

	/**
	 * 获取随机数
	 *
	 * @param count 随机数长度
	 * @return 随机数
	 */
	public static String getRandomStr(int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; ++i) {
			sb.append(RANDOM_BASE.charAt(RANDOM.nextInt(RANDOM_BASE.length())));
		}
		return sb.toString();
	}

	/**
	 * integer 转换为 byte[]
	 *
	 * @param source int
	 * @return byte[]
	 */
	public static byte[] intToBytes(int source) {
		return new byte[]{(byte) ((source >> 24) & 0xFF), (byte) ((source >> 16) & 0xFF), (byte) ((source >> 8) & 0xFF),
				(byte) (source & 0xFF)};
	}

	/**
	 * byte[] 转 integer
	 *
	 * @param byteArr 源数据
	 * @return int
	 */
	public static int bytesToInt(byte[] byteArr) {
		int count = 0;

		for (int i = 0; i < 4; ++i) {
			count <<= 8;
			count |= byteArr[i] & 255;
		}

		return count;
	}

	/**
	 * 对参数进行MD5散列
	 *
	 * @param params 参数
	 * @return 签名
	 */
	public static String getSignature(String... params) {
		return getSignature(CHARSET, params);
	}

	/**
	 * 对参数进行MD5散列
	 *
	 * @param params 参数
	 * @return 签名
	 * @throws NoSuchAlgorithmException 签名异常
	 */
	public static String getSignature(Charset charset, String... params) {
		Arrays.sort(params);
		StringBuilder stringBuffer = new StringBuilder();

		for (int i = 0; i < 4; ++i) {
			stringBuffer.append(params[i]);
		}

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			log.error("MessageDigest.getInstance exception", e);
			return null;
		}
		byte[] digest = md.digest(stringBuffer.toString().getBytes(charset));

		return toHexString(digest);
	}
}
